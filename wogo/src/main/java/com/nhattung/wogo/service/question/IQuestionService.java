package com.nhattung.wogo.service.question;

import com.nhattung.wogo.dto.request.QuestionRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionResponseDTO;
import com.nhattung.wogo.dto.response.QuestionUpdateResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IQuestionService {

    QuestionResponseDTO saveQuestion(QuestionRequestDTO requestDTO, MultipartFile imageFile);
    QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO, MultipartFile imageFile);
    QuestionUpdateResponseDTO getQuestionById(Long id);
    void deleteQuestion(Long id);
    PageResponse<QuestionResponseDTO> getAllQuestions(int page, int size);
    List<QuestionResponseDTO> findRandomQuestions(Long categoryId);
}
