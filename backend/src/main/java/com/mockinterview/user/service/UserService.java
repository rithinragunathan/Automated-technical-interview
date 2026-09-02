package com.mockinterview.user.service;

import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.user.dto.CandidateProfileDto;
import com.mockinterview.user.dto.UpdateProfileRequest;
import com.mockinterview.user.entity.CandidateProfile;
import com.mockinterview.user.repository.CandidateProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;

    public UserService(UserRepository userRepository, CandidateProfileRepository candidateProfileRepository) {
        this.userRepository = userRepository;
        this.candidateProfileRepository = candidateProfileRepository;
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public CandidateProfileDto updateProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
            userRepository.save(user);
        }

        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (request.getCollege() != null) profile.setCollege(request.getCollege());
        if (request.getDepartment() != null) profile.setDepartment(request.getDepartment());
        if (request.getYearOfStudy() != null) profile.setYearOfStudy(request.getYearOfStudy());
        if (request.getSkills() != null) profile.setSkills(String.join(",", request.getSkills()));
        if (request.getPreferredLanguage() != null) profile.setPreferredLanguage(request.getPreferredLanguage());
        if (request.getGithubUrl() != null) profile.setGithubUrl(request.getGithubUrl());
        if (request.getLinkedinUrl() != null) profile.setLinkedinUrl(request.getLinkedinUrl());
        if (request.getBio() != null) profile.setBio(request.getBio());
        if (request.getAvatarUrl() != null) profile.setAvatarUrl(request.getAvatarUrl());

        candidateProfileRepository.save(profile);
        return mapToDto(profile, user);
    }
    
    public CandidateProfileDto getMyProfile() {
        User user = getCurrentUser();
        CandidateProfile profile = candidateProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return mapToDto(profile, user);
    }

    private CandidateProfileDto mapToDto(CandidateProfile profile, User user) {
        CandidateProfileDto dto = new CandidateProfileDto();
        dto.setId(profile.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setCollege(profile.getCollege());
        dto.setDepartment(profile.getDepartment());
        dto.setYearOfStudy(profile.getYearOfStudy());
        if (profile.getSkills() != null && !profile.getSkills().isBlank()) {
            dto.setSkills(Arrays.asList(profile.getSkills().split(",")));
        } else {
            dto.setSkills(Collections.emptyList());
        }
        dto.setPreferredLanguage(profile.getPreferredLanguage());
        dto.setGithubUrl(profile.getGithubUrl());
        dto.setLinkedinUrl(profile.getLinkedinUrl());
        dto.setBio(profile.getBio());
        dto.setAvatarUrl(profile.getAvatarUrl());
        dto.setTotalInterviews(profile.getTotalInterviews());
        dto.setProblemsSolved(profile.getProblemsSolved());
        dto.setAverageScore(profile.getAverageScore());
        return dto;
    }
}
