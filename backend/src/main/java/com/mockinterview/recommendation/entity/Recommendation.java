package com.mockinterview.recommendation.entity;

import com.mockinterview.auth.entity.User;
import com.mockinterview.interview.entity.Interview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

@Entity
@Table(name = "recommendations")
@Getter
@Setter
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @Column(nullable = false, length = 100)
    private String category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int priority = 5;

    @Column(name = "resource_links", columnDefinition = "TEXT")
    private String resourceLinks;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public List<String> getResourceLinksList() {
        if (resourceLinks == null || resourceLinks.isEmpty()) return new ArrayList<>();
        return Arrays.asList(resourceLinks.split(";;"));
    }
    
    public void setResourceLinksList(List<String> links) {
        if (links == null || links.isEmpty()) resourceLinks = "";
        else resourceLinks = String.join(";;", links);
    }
}
