ALTER TABLE candidate_profiles
    MODIFY average_score DOUBLE NOT NULL DEFAULT 0;

ALTER TABLE test_cases
    MODIFY weight DOUBLE NOT NULL DEFAULT 1.0;

ALTER TABLE technical_answers
    MODIFY accuracy_score DOUBLE,
    MODIFY relevance_score DOUBLE,
    MODIFY depth_score DOUBLE,
    MODIFY clarity_score DOUBLE,
    MODIFY overall_score DOUBLE;

ALTER TABLE scores
    MODIFY overall_score DOUBLE NOT NULL DEFAULT 0,
    MODIFY coding_score DOUBLE NOT NULL DEFAULT 0,
    MODIFY knowledge_score DOUBLE NOT NULL DEFAULT 0,
    MODIFY communication_score DOUBLE,
    MODIFY behavior_score DOUBLE;

ALTER TABLE score_components
    MODIFY component_value DOUBLE NOT NULL,
    MODIFY weight DOUBLE NOT NULL,
    MODIFY weighted_value DOUBLE NOT NULL;

ALTER TABLE skill_metrics
    MODIFY score DOUBLE NOT NULL;
