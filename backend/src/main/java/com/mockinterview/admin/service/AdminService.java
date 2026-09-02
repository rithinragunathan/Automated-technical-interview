package com.mockinterview.admin.service;

import com.mockinterview.admin.dto.AdminStatsDto;
import com.mockinterview.admin.dto.CreateProblemRequest;
import com.mockinterview.admin.dto.CreateQuestionRequest;
import com.mockinterview.admin.dto.CreateTestCaseRequest;
import com.mockinterview.auth.entity.User;
import com.mockinterview.auth.repository.UserRepository;
import com.mockinterview.coding.entity.CodingProblem;
import com.mockinterview.coding.entity.TestCase;
import com.mockinterview.coding.repository.CodingProblemRepository;
import com.mockinterview.coding.repository.TestCaseRepository;
import com.mockinterview.common.exception.ResourceNotFoundException;
import com.mockinterview.interview.repository.InterviewRepository;
import com.mockinterview.question.entity.Question;
import com.mockinterview.question.repository.QuestionRepository;
import com.mockinterview.submission.repository.SubmissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class AdminService {

    private final CodingProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final InterviewRepository interviewRepository;
    private final SubmissionRepository submissionRepository;

    public AdminService(CodingProblemRepository problemRepository,
                        TestCaseRepository testCaseRepository,
                        QuestionRepository questionRepository,
                        UserRepository userRepository,
                        InterviewRepository interviewRepository,
                        SubmissionRepository submissionRepository) {
        this.problemRepository = problemRepository;
        this.testCaseRepository = testCaseRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.interviewRepository = interviewRepository;
        this.submissionRepository = submissionRepository;
    }

    public CodingProblem createProblem(CreateProblemRequest request) {
        CodingProblem p = new CodingProblem();
        p.setTitle(request.title());
        p.setSlug(request.slug());
        p.setDescription(request.description());
        p.setDifficulty(request.difficulty());
        p.setTopic(request.topic());
        p.setTags(request.tags());
        p.setConstraints(request.constraints());
        p.setInputFormat(request.inputFormat());
        p.setOutputFormat(request.outputFormat());
        p.setExamples(request.examples());
        p.setTimeComplexityHint(request.timeComplexityHint());
        p.setSpaceComplexityHint(request.spaceComplexityHint());
        p.setStarterCodeJava(request.starterCodeJava());
        p.setStarterCodePython(request.starterCodePython());
        p.setStarterCodeCpp(request.starterCodeCpp());

        CodingProblem saved = problemRepository.save(p);

        if (request.testCases() != null) {
            for (CreateTestCaseRequest tcReq : request.testCases()) {
                TestCase tc = new TestCase();
                tc.setProblem(saved);
                tc.setInput(tcReq.input());
                tc.setExpectedOutput(tcReq.expectedOutput());
                tc.setHidden(tcReq.hidden());
                tc.setWeight(tcReq.weight());
                tc.setDescription(tcReq.description());
                testCaseRepository.save(tc);
            }
        }
        return saved;
    }

    public CodingProblem updateProblem(Long id, CreateProblemRequest request) {
        CodingProblem p = problemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Problem not found"));
        p.setTitle(request.title());
        p.setSlug(request.slug());
        p.setDescription(request.description());
        p.setDifficulty(request.difficulty());
        p.setTopic(request.topic());
        p.setTags(request.tags());
        p.setConstraints(request.constraints());
        p.setInputFormat(request.inputFormat());
        p.setOutputFormat(request.outputFormat());
        p.setExamples(request.examples());
        p.setTimeComplexityHint(request.timeComplexityHint());
        p.setSpaceComplexityHint(request.spaceComplexityHint());
        p.setStarterCodeJava(request.starterCodeJava());
        p.setStarterCodePython(request.starterCodePython());
        p.setStarterCodeCpp(request.starterCodeCpp());
        
        // Note: For simplicity, not updating test cases here. In production, we'd handle test case updates.
        return problemRepository.save(p);
    }

    public void deleteProblem(Long id) {
        if (!problemRepository.existsById(id)) throw new ResourceNotFoundException("Problem not found");
        problemRepository.deleteById(id);
    }

    public Question createQuestion(CreateQuestionRequest request) {
        Question q = new Question();
        q.setTitle(request.title());
        q.setContent(request.content());
        q.setQuestionType(request.questionType());
        q.setDifficulty(request.difficulty());
        q.setTopic(request.topic());
        q.setTags(request.tags());
        q.setExpectedAnswer(request.expectedAnswer());
        q.setEvaluationRubric(request.evaluationRubric());
        return questionRepository.save(q);
    }

    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public AdminStatsDto getStats() {
        return new AdminStatsDto(
                userRepository.count(),
                interviewRepository.count(),
                problemRepository.count(),
                submissionRepository.count(),
                0.0 // To implement avg overall score logic
        );
    }
}
