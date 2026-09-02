CREATE TYPE interview_status AS ENUM ('PENDING', 'ACTIVE', 'COMPLETED', 'ABANDONED');

CREATE TABLE interviews (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    interview_type interview_type NOT NULL,
    difficulty difficulty_level NOT NULL,
    topic VARCHAR(100),
    status interview_status NOT NULL DEFAULT 'PENDING',
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration_seconds INTEGER,
    questions_attempted INTEGER NOT NULL DEFAULT 0,
    questions_completed INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE interview_questions (
    id BIGSERIAL PRIMARY KEY,
    interview_id BIGINT NOT NULL REFERENCES interviews(id) ON DELETE CASCADE,
    question_id BIGINT REFERENCES questions(id),
    coding_problem_id BIGINT REFERENCES coding_problems(id),
    sequence_number INTEGER NOT NULL,
    asked_at TIMESTAMP,
    completed_at TIMESTAMP,
    ai_generated BOOLEAN NOT NULL DEFAULT FALSE,
    ai_question_text TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_interviews_user_id ON interviews(user_id);
CREATE INDEX idx_interviews_status ON interviews(status);
CREATE INDEX idx_interview_questions_interview_id ON interview_questions(interview_id);
