package com.mockinterview.interview.service;

import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.interview.dto.InterviewDto;
import com.mockinterview.interview.dto.InterviewQuestionDto;
import com.mockinterview.interview.dto.InterviewSummaryDto;
import com.mockinterview.interview.dto.StartInterviewRequest;
import com.mockinterview.interview.entity.Interview;
import com.mockinterview.interview.entity.InterviewQuestion;
import com.mockinterview.interview.repository.InterviewQuestionRepository;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.question.entity.Question;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final UserRepository userRepository;

    public InterviewService(InterviewRepository interviewRepository,
                            InterviewQuestionRepository interviewQuestionRepository,
                            UserRepository userRepository) {
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.userRepository = userRepository;
    }

    public InterviewDto createInterview(Long userId, StartInterviewRequest request) {
        Interview interview = new Interview();
        interview.setUser(userRepository.getReferenceById(userId));
        interview.setInterviewType(Interview.InterviewType.valueOf(request.getInterviewType().toUpperCase()));
        interview.setDifficulty(Question.DifficultyLevel.valueOf(request.getDifficulty().toUpperCase()));
        interview.setTopic(request.getTopic());
        interview.setStatus(Interview.InterviewStatus.PENDING);
        
        Interview saved = interviewRepository.save(interview);
        
        // In a real implementation we would call EvaluationService / LLMProvider here
        // to generate/fetch Questions and save InterviewQuestion entities.
        
        return mapToDto(saved);
    }

    public InterviewDto startInterview(Long interviewId, Long userId) {
        Interview interview = getInterview(interviewId, userId);
        interview.setStatus(Interview.InterviewStatus.ACTIVE);
        interview.setStartTime(LocalDateTime.now());
        return mapToDto(interviewRepository.save(interview));
    }

    public InterviewDto finishInterview(Long interviewId, Long userId) {
        Interview interview = getInterview(interviewId, userId);
        interview.setStatus(Interview.InterviewStatus.COMPLETED);
        interview.setEndTime(LocalDateTime.now());
        
        if (interview.getStartTime() != null) {
            interview.setDurationSeconds((int) Duration.between(interview.getStartTime(), interview.getEndTime()).getSeconds());
        }
        
        // ReportService.generateReport(interviewId) would be triggered here (e.g., via events or direct call)
        
        return mapToDto(interviewRepository.save(interview));
    }

    public InterviewDto getInterviewById(Long id, Long userId) {
        return mapToDto(getInterview(id, userId));
    }

    public List<InterviewSummaryDto> getUserInterviews(Long userId) {
        return interviewRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(i -> new InterviewSummaryDto(
                        i.getId(),
                        i.getInterviewType().name(),
                        i.getDifficulty().name(),
                        i.getTopic(),
                        i.getStatus().name(),
                        i.getStartTime(),
                        i.getEndTime(),
                        null, // overallScore to be fetched from ScoreRepository
                        i.getCreatedAt()
                )).collect(Collectors.toList());
    }
    
    private Interview getInterview(Long id, Long userId) {
        Interview interview = interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));
        if (!interview.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Interview not found");
        }
        return interview;
    }

    private InterviewDto mapToDto(Interview i) {
        List<InterviewQuestionDto> questions = interviewQuestionRepository.findByInterviewIdOrderBySequenceNumber(i.getId())
                .stream().map(iq -> new InterviewQuestionDto(
                        iq.getId(),
                        iq.getSequenceNumber(),
                        iq.isAiGenerated() ? iq.getAiQuestionText() : (iq.getQuestion() != null ? iq.getQuestion().getContent() : ""),
                        iq.getQuestion() != null ? iq.getQuestion().getQuestionType().name() : "AI_GENERATED",
                        iq.getCodingProblem() != null ? iq.getCodingProblem().getId() : null,
                        iq.getAskedAt(),
                        iq.getCompletedAt()
                )).collect(Collectors.toList());

        return new InterviewDto(
                i.getId(),
                i.getUser().getId(),
                i.getInterviewType().name(),
                i.getDifficulty().name(),
                i.getTopic(),
                i.getStatus().name(),
                i.getStartTime(),
                i.getEndTime(),
                i.getDurationSeconds(),
                i.getQuestionsAttempted(),
                i.getQuestionsCompleted(),
                i.getCreatedAt(),
                questions
        );
    }
}
