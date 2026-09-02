package com.mockinterview.evaluation.service;

import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.evaluation.dto.AnswerEvaluationResult;
import com.mockinterview.evaluation.entity.TechnicalAnswer;
import com.mockinterview.evaluation.provider.LLMProvider;
import com.mockinterview.evaluation.repository.TechnicalAnswerRepository;
import com.mockinterview.interview.entity.InterviewQuestion;
import com.mockinterview.interview.repository.InterviewQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class EvaluationService {

    private final LLMProvider llmProvider;
    private final TechnicalAnswerRepository technicalAnswerRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    public EvaluationService(LLMProvider llmProvider,
                             TechnicalAnswerRepository technicalAnswerRepository,
                             InterviewQuestionRepository interviewQuestionRepository) {
        this.llmProvider = llmProvider;
        this.technicalAnswerRepository = technicalAnswerRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
    }

    public AnswerEvaluationResult evaluateAnswer(Long interviewId, Long interviewQuestionId, String answerText) {
        InterviewQuestion iq = interviewQuestionRepository.findById(interviewQuestionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        if (!iq.getInterview().getId().equals(interviewId)) {
            throw new ResourceNotFoundException("Interview question mismatch");
        }

        String qText = iq.isAiGenerated() ? iq.getAiQuestionText() : (iq.getQuestion() != null ? iq.getQuestion().getContent() : "");

        AnswerEvaluationResult result = llmProvider.evaluateAnswer(qText, answerText, "");

        TechnicalAnswer ta = new TechnicalAnswer();
        ta.setInterview(iq.getInterview());
        ta.setInterviewQuestion(iq);
        ta.setAnswerText(answerText);
        ta.setAccuracyScore(result.accuracyScore());
        ta.setRelevanceScore(result.relevanceScore());
        ta.setDepthScore(result.depthScore());
        ta.setClarityScore(result.clarityScore());
        ta.setOverallScore(result.overallScore());
        ta.setAiFeedback(result.feedback());

        technicalAnswerRepository.save(ta);

        iq.setCompletedAt(java.time.LocalDateTime.now());
        interviewQuestionRepository.save(iq);

        return result;
    }
}
