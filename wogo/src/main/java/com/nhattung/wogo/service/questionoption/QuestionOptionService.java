package com.nhattung.wogo.service.questionoption;

import com.nhattung.wogo.dto.request.QuestionOptionRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionOptionResponseDTO;
import com.nhattung.wogo.entity.Question;
import com.nhattung.wogo.entity.QuestionOption;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.QuestionOptionRepository;
import com.nhattung.wogo.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionOptionService implements IQuestionOptionService{

    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    @Override
    public QuestionOptionResponseDTO saveOption(QuestionOptionRequestDTO request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        QuestionOption savedOption = questionOptionRepository.save(createQuestionOption(request, question));

        return convertToResponseDTO(savedOption);
    }

    private QuestionOption createQuestionOption(QuestionOptionRequestDTO request, Question question) {
        return QuestionOption.builder()
                .question(question)
                .optionText(request.getOptionText())
                .isCorrect(request.isCorrect())
                .orderIndex(request.getOrderIndex())
                .build();
    }

    @Override
    public QuestionOptionResponseDTO updateOption(Long id, QuestionOptionRequestDTO request) {
        QuestionOption existingOption = questionOptionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_OPTION_NOT_FOUND));

        QuestionOption updatedOption = updateExistingOption(existingOption, request);

        return convertToResponseDTO(updatedOption);
    }

    private QuestionOption updateExistingOption(QuestionOption existingOption, QuestionOptionRequestDTO request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        existingOption.setQuestion(question);
        existingOption.setOptionText(request.getOptionText());
        existingOption.setCorrect(request.isCorrect());
        existingOption.setOrderIndex(request.getOrderIndex());

        return questionOptionRepository.save(existingOption);
    }

    @Override
    public void deleteOption(Long id) {
        questionOptionRepository.findById(id).ifPresentOrElse(
                questionOptionRepository::delete,
                () -> {
                    throw new AppException(ErrorCode.QUESTION_OPTION_NOT_FOUND);
                }
        );
    }

    @Override
    public QuestionOptionResponseDTO getOptionById(Long id) {
        return questionOptionRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_OPTION_NOT_FOUND));
    }

    @Override
    public PageResponse<QuestionOptionResponseDTO> getAllOptions(int page, int size) {
        if(page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<QuestionOption> questionOptionPagePage = questionOptionRepository.findAll(pageable);

        List<QuestionOptionResponseDTO> questionOptionResponseDTOList = questionOptionPagePage.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return PageResponse.<QuestionOptionResponseDTO>builder()
                .currentPage(page)
                .totalPages(questionOptionPagePage.getTotalPages())
                .totalElements(questionOptionPagePage.getTotalElements())
                .pageSize(size)
                .data(questionOptionResponseDTOList)
                .build();
    }


    private QuestionOptionResponseDTO convertToResponseDTO(QuestionOption questionOption) {
        QuestionOptionResponseDTO responseDTO = modelMapper.map(questionOption, QuestionOptionResponseDTO.class);
        responseDTO.setQuestionId(questionOption.getQuestion().getId());
        return responseDTO;
    }
}
