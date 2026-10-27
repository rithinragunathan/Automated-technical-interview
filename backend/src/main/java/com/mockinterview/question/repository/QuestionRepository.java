package com.mockinterview.question.repository;

import com.mockinterview.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuestionTypeAndDifficulty(Question.QuestionType type, Question.DifficultyLevel difficulty);
    List<Question> findByQuestionTypeAndDifficultyAndActiveTrue(Question.QuestionType type, Question.DifficultyLevel difficulty);
    List<Question> findByQuestionTypeAndActiveTrue(Question.QuestionType type);
    List<Question> findByTopic(String topic);

    @Query(value = "SELECT * FROM questions q WHERE q.question_type = :type AND q.difficulty = :difficulty AND q.active = true ORDER BY RAND() LIMIT :count", nativeQuery = true)
    List<Question> findRandom(@Param("type") String type, @Param("difficulty") String difficulty, @Param("count") int count);
}
