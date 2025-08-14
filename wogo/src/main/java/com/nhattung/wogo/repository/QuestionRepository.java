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
        (
          SELECT * FROM question
          WHERE difficulty_level = 'EASY' AND question_category_id = :categoryId
          ORDER BY RAND()
          LIMIT 10
        )
        UNION ALL
        (
          SELECT * FROM question
          WHERE difficulty_level = 'MEDIUM' AND question_category_id = :categoryId
          ORDER BY RAND()
          LIMIT 7
        )
        UNION ALL
        (
          SELECT * FROM question
          WHERE difficulty_level = 'HARD' AND question_category_id = :categoryId
          ORDER BY RAND()
          LIMIT 3
        )
        """, nativeQuery = true)
    List<Question> findRandomQuestions(Long categoryId);

}
