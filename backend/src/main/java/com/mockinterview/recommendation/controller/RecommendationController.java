package com.mockinterview.recommendation.controller;

import com.mockinterview.common.response.ApiResponse;
import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.recommendation.entity.Recommendation;
import com.mockinterview.recommendation.dto.RecommendationDto;
import com.mockinterview.recommendation.repository.RecommendationRepository;
import com.mockinterview.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PatchMapping("/{id}/completion")
    public ResponseEntity<ApiResponse<RecommendationDto>> updateCompletion(
            @PathVariable Long id,
            @RequestParam boolean completed
    ) {
        Long userId = userService.getCurrentUser().getId();
        Recommendation recommendation = recommendationRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found"));
        recommendation.setCompleted(completed);
        Recommendation saved = recommendationRepository.save(recommendation);
        RecommendationDto dto = new RecommendationDto(
                saved.getId(), saved.getCategory(), saved.getTitle(), saved.getDescription(),
                saved.getPriority(), saved.getResourceLinksList(), saved.isCompleted()
        );
        return ResponseEntity.ok(ApiResponse.success(dto, "Recommendation updated"));
    }
}
