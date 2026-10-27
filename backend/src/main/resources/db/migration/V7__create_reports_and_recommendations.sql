CREATE TABLE feedback_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL UNIQUE REFERENCES interviews(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    overall_summary TEXT,
    strengths TEXT,
    weaknesses TEXT,
    ai_narrative TEXT,
    generated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    interview_id BIGINT REFERENCES interviews(id),
    category VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    priority INTEGER NOT NULL DEFAULT 5,
    resource_links TEXT,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE skill_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    interview_id BIGINT REFERENCES interviews(id),
    skill_name VARCHAR(100) NOT NULL,
    score NUMERIC(5,2) NOT NULL,
    recorded_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_feedback_reports_interview_id ON feedback_reports(interview_id);
CREATE INDEX idx_recommendations_user_id ON recommendations(user_id);
CREATE INDEX idx_skill_metrics_user_id ON skill_metrics(user_id);
CREATE INDEX idx_skill_metrics_skill_name ON skill_metrics(skill_name);
