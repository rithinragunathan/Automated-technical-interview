package com.mockinterview.user.dto;

import lombok.Data;
import java.util.List;

@Data
public class CandidateProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private String college;
    private String department;
    private Integer yearOfStudy;
    private List<String> skills;
    private String preferredLanguage;
    private String githubUrl;
    private String linkedinUrl;
    private String bio;
    private String avatarUrl;
    private int totalInterviews;
    private int problemsSolved;
    private double averageScore;
}
