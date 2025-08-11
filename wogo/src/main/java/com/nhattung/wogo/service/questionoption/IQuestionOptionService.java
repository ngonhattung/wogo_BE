package com.nhattung.wogo.service.questionoption;

import com.nhattung.wogo.dto.request.QuestionOptionRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionOptionResponseDTO;

public interface IQuestionOptionService {

    QuestionOptionResponseDTO saveOption(QuestionOptionRequestDTO request);
    QuestionOptionResponseDTO updateOption(Long id, QuestionOptionRequestDTO request);
    void deleteOption(Long id);
    QuestionOptionResponseDTO getOptionById(Long id);
    PageResponse<QuestionOptionResponseDTO> getAllOptions(int page, int size);


}
