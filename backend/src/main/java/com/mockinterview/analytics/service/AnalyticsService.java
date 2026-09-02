package com.mockinterview.analytics.service;

import com.mockinterview.analytics.dto.AnalyticsDashboardDto;
import com.mockinterview.analytics.dto.ScoreHistoryPoint;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.scoring.repository.ScoreRepository;
import com.mockinterview.submission.entity.Submission;
import com.mockinterview.submission.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final InterviewRepository interviewRepository;
    private final ScoreRepository scoreRepository;
    private final SubmissionRepository submissionRepository;

    public AnalyticsService(InterviewRepository interviewRepository,
                            ScoreRepository scoreRepository,
                            SubmissionRepository submissionRepository) {
        this.interviewRepository = interviewRepository;
        this.scoreRepository = scoreRepository;
        this.submissionRepository = submissionRepository;
    }

    public AnalyticsDashboardDto getDashboard(Long userId) {
        List<Interview> interviews = interviewRepository.findByUserIdAndStatus(userId, Interview.InterviewStatus.COMPLETED);
        
        List<ScoreHistoryPoint> history = new ArrayList<>();
        for (Interview i : interviews) {
            scoreRepository.findByInterviewId(i.getId()).ifPresent(s -> {
                history.add(new ScoreHistoryPoint(i.getId(), i.getCreatedAt(), s.getOverallScore(), s.getCodingScore(), s.getKnowledgeScore()));
            });
        }

        List<Submission> submissions = submissionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        Map<String, Double> topicPerformance = new HashMap<>();
        Map<String, Double> diffPerformance = new HashMap<>();
        
        int totalPassed = 0;
        int totalTests = 0;
        long totalExecutionTime = 0;
        int execCount = 0;
        
        for (Submission s : submissions) {
            String topic = s.getProblem().getTopic();
            String diff = s.getProblem().getDifficulty().name();
            
            totalPassed += s.getPassedTests();
            totalTests += s.getTotalTests();
            if (s.getExecutionTimeMs() != null) {
                totalExecutionTime += s.getExecutionTimeMs();
                execCount++;
            }
            
            double score = (s.getTotalTests() > 0) ? (s.getPassedTests() * 100.0 / s.getTotalTests()) : 0.0;
            
            topicPerformance.merge(topic != null ? topic : "Unknown", score, (a,b) -> (a+b)/2);
            diffPerformance.merge(diff, score, (a,b) -> (a+b)/2);
        }
        
        double avgTime = execCount > 0 ? (double) totalExecutionTime / execCount : 0.0;
        double successRate = totalTests > 0 ? (totalPassed * 100.0) / totalTests : 0.0;

        List<String> weakest = topicPerformance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> strongest = topicPerformance.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return new AnalyticsDashboardDto(
                history, topicPerformance, diffPerformance, avgTime, successRate,
                weakest, strongest, interviews.size(), submissions.size()
        );
    }
}
