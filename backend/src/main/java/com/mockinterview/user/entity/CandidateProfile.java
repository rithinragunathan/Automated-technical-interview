package com.mockinterview.user.entity;

import com.mockinterview.auth.entity.User;
import com.mockinterview.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
public class CandidateProfile extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String college;
    private String department;
    private Integer yearOfStudy;
    
    // Storing skills as a comma-separated string for simplicity
    private String skills;
    
    private String preferredLanguage = "java";
    private String githubUrl;
    private String linkedinUrl;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    private String avatarUrl;
    
    @Column(nullable = false)
    private int totalInterviews = 0;
    
    @Column(nullable = false)
    private int problemsSolved = 0;
    
    @Column(nullable = false)
    private double averageScore = 0.0;
}
