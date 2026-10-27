CREATE TABLE technical_answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL REFERENCES interviews(id) ON DELETE CASCADE,
    interview_question_id BIGINT NOT NULL REFERENCES interview_questions(id) ON DELETE CASCADE,
    answer_text TEXT NOT NULL,
    accuracy_score NUMERIC(5,2),
    relevance_score NUMERIC(5,2),
    depth_score NUMERIC(5,2),
    clarity_score NUMERIC(5,2),
    overall_score NUMERIC(5,2),
    ai_feedback TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE scores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interview_id BIGINT NOT NULL UNIQUE REFERENCES interviews(id) ON DELETE CASCADE,
    overall_score NUMERIC(5,2) NOT NULL DEFAULT 0,
    coding_score NUMERIC(5,2) NOT NULL DEFAULT 0,
    knowledge_score NUMERIC(5,2) NOT NULL DEFAULT 0,
    communication_score NUMERIC(5,2),
    behavior_score NUMERIC(5,2),
    proficiency_level VARCHAR(50),
    calculated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE score_components (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    score_id BIGINT NOT NULL REFERENCES scores(id) ON DELETE CASCADE,
    component_name VARCHAR(100) NOT NULL,
    component_value NUMERIC(5,2) NOT NULL,
    weight NUMERIC(5,4) NOT NULL,
    weighted_value NUMERIC(5,2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_scores_interview_id ON scores(interview_id);
CREATE INDEX idx_score_components_score_id ON score_components(score_id);
