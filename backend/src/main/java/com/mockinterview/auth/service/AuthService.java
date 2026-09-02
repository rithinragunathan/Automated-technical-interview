package com.mockinterview.auth.service;

import com.mockinterview.auth.dto.AuthResponse;
import com.mockinterview.auth.dto.LoginRequest;
import com.mockinterview.auth.dto.RegisterRequest;
import com.mockinterview.auth.entity.Role;
import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.RoleRepository;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.auth.security.JwtUtil;
import com.mockinterview.common.exception.ConflictException;
import com.mockinterview.user.entity.CandidateProfile;
import com.mockinterview.user.repository.CandidateProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       CandidateProfileRepository candidateProfileRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.candidateProfileRepository = candidateProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already in use");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        
        Role role = roleRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_STUDENT");
                    return roleRepository.save(newRole);
                });
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);

        CandidateProfile profile = new CandidateProfile();
        profile.setUser(savedUser);
        profile.setCollege(request.getCollege());
        profile.setDepartment(request.getDepartment());
        profile.setYearOfStudy(request.getYearOfStudy());
        if (request.getSkills() != null) {
            profile.setSkills(String.join(",", request.getSkills()));
        }
        profile.setPreferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "java");
        
        candidateProfileRepository.save(profile);

        String token = jwtUtil.generateToken(savedUser);
        return new AuthResponse(token, "Bearer", savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), "ROLE_STUDENT");
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);
        
        String roleStr = user.getRoles().stream().findFirst().map(Role::getName).orElse("ROLE_STUDENT");
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), user.getFullName(), roleStr);
    }
}
