CREATE TABLE candidate_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    college VARCHAR(255),
    department VARCHAR(255),
    year_of_study INTEGER,
    skills TEXT,
    preferred_language VARCHAR(50) DEFAULT 'java',
    github_url VARCHAR(500),
    linkedin_url VARCHAR(500),
    bio TEXT,
    avatar_url VARCHAR(500),
    total_interviews INTEGER NOT NULL DEFAULT 0,
    problems_solved INTEGER NOT NULL DEFAULT 0,
    average_score NUMERIC(5,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_candidate_profiles_user_id ON candidate_profiles(user_id);
