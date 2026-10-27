package com.mockinterview.coding.service;

import com.mockinterview.coding.dto.CodingProblemDto;
import com.mockinterview.coding.dto.CodingProblemSummaryDto;
import com.mockinterview.coding.dto.TestCaseDto;
import com.mockinterview.coding.entity.CodingProblem;
import com.mockinterview.coding.repository.CodingProblemRepository;
import com.mockinterview.coding.repository.TestCaseRepository;
import com.mockinterview.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CodingProblemService {

    private final CodingProblemRepository codingProblemRepository;
    private final TestCaseRepository testCaseRepository;

    public CodingProblemService(CodingProblemRepository codingProblemRepository, TestCaseRepository testCaseRepository) {
        this.codingProblemRepository = codingProblemRepository;
        this.testCaseRepository = testCaseRepository;
    }

    public Page<CodingProblemSummaryDto> getProblems(Pageable pageable, String difficulty, String topic, String search) {
        // Basic implementation, for production we'd use Specifications or QueryDSL
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase();
        List<CodingProblemSummaryDto> problems = codingProblemRepository.findAllByActiveTrue().stream()
                .filter(p -> difficulty == null || p.getDifficulty().name().equalsIgnoreCase(difficulty))
                .filter(p -> topic == null || (p.getTopic() != null && p.getTopic().equalsIgnoreCase(topic)))
                .filter(p -> normalizedSearch.isEmpty()
                        || p.getTitle().toLowerCase().contains(normalizedSearch)
                        || (p.getTopic() != null && p.getTopic().toLowerCase().contains(normalizedSearch))
                        || (p.getTags() != null && p.getTags().toLowerCase().contains(normalizedSearch)))
                .map(p -> new CodingProblemSummaryDto(
                        p.getId(),
                        p.getTitle(),
                        p.getSlug(),
                        p.getDifficulty().name(),
                        p.getTopic(),
                        p.getTags() != null ? Arrays.asList(p.getTags().split(",")) : Collections.emptyList()
                ))
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), problems.size());
        
        if (start >= problems.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, problems.size());
        }
        
        return new PageImpl<>(problems.subList(start, end), pageable, problems.size());
    }

    public CodingProblemDto getProblemById(Long id) {
        CodingProblem problem = codingProblemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with id: " + id));
        return mapToDto(problem);
    }

    public CodingProblemDto getProblemBySlug(String slug) {
        CodingProblem problem = codingProblemRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Problem not found with slug: " + slug));
        return mapToDto(problem);
    }

    private CodingProblemDto mapToDto(CodingProblem p) {
        List<TestCaseDto> publicTestCases = testCaseRepository.findByProblemIdAndHiddenFalse(p.getId()).stream()
                .map(tc -> new TestCaseDto(tc.getId(), tc.getInput(), tc.getExpectedOutput(), tc.getDescription()))
                .collect(Collectors.toList());

        return new CodingProblemDto(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getDescription(),
                p.getDifficulty().name(),
                p.getTopic(),
                p.getTags() != null ? Arrays.asList(p.getTags().split(",")) : Collections.emptyList(),
                p.getConstraints(),
                p.getInputFormat(),
                p.getOutputFormat(),
                p.getExamples(),
                p.getTimeComplexityHint(),
                p.getSpaceComplexityHint(),
                p.getStarterCodeJava(),
                p.getStarterCodePython(),
                p.getStarterCodeCpp(),
                p.isActive(),
                publicTestCases
        );
    }
}
