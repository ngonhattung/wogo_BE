package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.QuestionOption;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    Optional<QuestionOption> findByQuestionId(Long questionId);
}
