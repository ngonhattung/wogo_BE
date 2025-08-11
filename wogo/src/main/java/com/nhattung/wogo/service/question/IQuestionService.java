package com.nhattung.wogo.service.question;

import com.nhattung.wogo.dto.request.QuestionRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionResponseDTO;
import com.nhattung.wogo.dto.response.QuestionUpdateResponseDTO;

public interface IQuestionService {

    QuestionResponseDTO saveQuestion(QuestionRequestDTO requestDTO);
    QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO);
    QuestionUpdateResponseDTO getQuestionById(Long id);
    void deleteQuestion(Long id);
    PageResponse<QuestionResponseDTO> getAllQuestions(int page, int size);
}
