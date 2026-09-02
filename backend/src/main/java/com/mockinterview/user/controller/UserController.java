package com.mockinterview.user.controller;

import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.user.dto.CandidateProfileDto;
import com.mockinterview.user.dto.UpdateProfileRequest;
import com.mockinterview.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> getMyProfile() {
        return ResponseEntity.ok(ApiResponse.success(userService.getMyProfile(), "Profile retrieved"));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CandidateProfileDto>> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userService.updateProfile(request), "Profile updated"));
    }
}
