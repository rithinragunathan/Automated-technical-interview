package com.mockinterview.report.entity;

import com.mockinterview.auth.entity.User;
import com.mockinterview.interview.entity.Interview;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Entity
@Table(name = "feedback_reports")
@Getter
@Setter
public class FeedbackReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false, unique = true)
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "overall_summary", columnDefinition = "TEXT")
    private String overallSummary;

    // We store as comma separated for simplicity in this implementation
    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(name = "ai_narrative", columnDefinition = "TEXT")
    private String aiNarrative;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt = LocalDateTime.now();
    
    public List<String> getStrengthsList() {
        if (strengths == null || strengths.isEmpty()) return new ArrayList<>();
        return Arrays.asList(strengths.split(";;"));
    }
    
    public void setStrengthsList(List<String> list) {
        if (list == null || list.isEmpty()) strengths = "";
        else strengths = String.join(";;", list);
    }
    
    public List<String> getWeaknessesList() {
        if (weaknesses == null || weaknesses.isEmpty()) return new ArrayList<>();
        return Arrays.asList(weaknesses.split(";;"));
    }
    
    public void setWeaknessesList(List<String> list) {
        if (list == null || list.isEmpty()) weaknesses = "";
        else weaknesses = String.join(";;", list);
    }
}
