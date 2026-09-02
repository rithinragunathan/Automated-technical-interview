package com.mockinterview.recommendation.controller;

import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.recommendation.dto.RecommendationDto;
import com.mockinterview.recommendation.repository.RecommendationRepository;
import com.mockinterview.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationRepository recommendationRepository;
    private final UserService userService;

    public RecommendationController(RecommendationRepository recommendationRepository, UserService userService) {
        this.recommendationRepository = recommendationRepository;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RecommendationDto>>> getRecommendations() {
        Long userId = userService.getCurrentUser().getId();
        List<RecommendationDto> dtos = recommendationRepository.findByUserIdOrderByPriorityAsc(userId)
                .stream().map(r -> new RecommendationDto(
                        r.getId(), r.getCategory(), r.getTitle(), r.getDescription(),
                        r.getPriority(), r.getResourceLinksList(), r.isCompleted()
                )).collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(dtos, "Recommendations retrieved"));
    }
}
