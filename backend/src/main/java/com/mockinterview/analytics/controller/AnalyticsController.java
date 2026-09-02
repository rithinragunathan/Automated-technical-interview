package com.mockinterview.analytics.controller;

import com.mockinterview.analytics.dto.AnalyticsDashboardDto;
import com.mockinterview.analytics.service.AnalyticsService;
import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;

    public AnalyticsController(AnalyticsService analyticsService, UserService userService) {
        this.analyticsService = analyticsService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AnalyticsDashboardDto>> getDashboard() {
        Long userId = userService.getCurrentUser().getId();
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getDashboard(userId), "Analytics retrieved"));
    }
}
