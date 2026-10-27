CREATE TABLE submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    problem_id BIGINT NOT NULL REFERENCES coding_problems(id),
    interview_id BIGINT REFERENCES interviews(id),
    language VARCHAR(50) NOT NULL DEFAULT 'java',
    source_code TEXT NOT NULL,
    execution_type VARCHAR(20) NOT NULL DEFAULT 'RUN',
    status VARCHAR(40) NOT NULL DEFAULT 'PENDING',
    passed_tests INTEGER NOT NULL DEFAULT 0,
    total_tests INTEGER NOT NULL DEFAULT 0,
    execution_time_ms BIGINT,
    memory_used_kb BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE execution_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    stdout TEXT,
    stderr TEXT,
    exit_code INTEGER,
    execution_time_ms BIGINT,
    memory_used_kb BIGINT,
    compilation_error TEXT,
    timed_out BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE test_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    submission_id BIGINT NOT NULL REFERENCES submissions(id) ON DELETE CASCADE,
    test_case_id BIGINT NOT NULL REFERENCES test_cases(id),
    passed BOOLEAN NOT NULL DEFAULT FALSE,
    actual_output TEXT,
    execution_time_ms BIGINT,
    memory_used_kb BIGINT,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_submissions_user_id ON submissions(user_id);
CREATE INDEX idx_submissions_problem_id ON submissions(problem_id);
CREATE INDEX idx_submissions_interview_id ON submissions(interview_id);
CREATE INDEX idx_submissions_status ON submissions(status);
CREATE INDEX idx_test_results_submission_id ON test_results(submission_id);
