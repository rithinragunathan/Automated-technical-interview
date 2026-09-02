CREATE TYPE question_type AS ENUM ('TECHNICAL', 'BEHAVIORAL', 'CODING', 'FOLLOW_UP');
CREATE TYPE difficulty_level AS ENUM ('EASY', 'MEDIUM', 'HARD');
CREATE TYPE interview_type AS ENUM ('CODING', 'TECHNICAL_QA', 'MIXED');

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    question_type question_type NOT NULL,
    difficulty difficulty_level NOT NULL DEFAULT 'MEDIUM',
    topic VARCHAR(100),
    tags TEXT[],
    expected_answer TEXT,
    evaluation_rubric TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE coding_problems (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    difficulty difficulty_level NOT NULL DEFAULT 'MEDIUM',
    topic VARCHAR(100),
    tags TEXT[],
    constraints TEXT,
    input_format TEXT,
    output_format TEXT,
    examples JSONB,
    time_complexity_hint VARCHAR(100),
    space_complexity_hint VARCHAR(100),
    starter_code_java TEXT,
    starter_code_python TEXT,
    starter_code_cpp TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE test_cases (
    id BIGSERIAL PRIMARY KEY,
    problem_id BIGINT NOT NULL REFERENCES coding_problems(id) ON DELETE CASCADE,
    input TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    weight NUMERIC(3,2) NOT NULL DEFAULT 1.0,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_questions_type ON questions(question_type);
CREATE INDEX idx_questions_difficulty ON questions(difficulty);
CREATE INDEX idx_coding_problems_difficulty ON coding_problems(difficulty);
CREATE INDEX idx_test_cases_problem_id ON test_cases(problem_id);
CREATE INDEX idx_test_cases_hidden ON test_cases(hidden);
