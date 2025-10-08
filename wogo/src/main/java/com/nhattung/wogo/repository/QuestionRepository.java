package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = """
    SELECT * FROM question
    WHERE question_category_id = :categoryId
      AND difficulty_level = :level
      AND id >= (
        SELECT FLOOR(MIN(id) + (MAX(id) - MIN(id)) * RAND())
        FROM question
        WHERE question_category_id = :categoryId AND difficulty_level = :level
      )
    ORDER BY id
    LIMIT :limit
    """, nativeQuery = true)
    List<Question> findRandomByDifficulty(Long categoryId, String level, int limit);

}
