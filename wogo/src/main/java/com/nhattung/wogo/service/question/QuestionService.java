package com.nhattung.wogo.service.question;

import com.nhattung.wogo.dto.request.QuestionOptionRequestDTO;
import com.nhattung.wogo.dto.request.QuestionRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.Question;
import com.nhattung.wogo.entity.QuestionCategory;
import com.nhattung.wogo.enums.DifficultyLevel;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.QuestionType;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.QuestionCategoryRepository;
import com.nhattung.wogo.repository.QuestionRepository;
import com.nhattung.wogo.repository.ServiceCategoryRepository;
import com.nhattung.wogo.service.questioncategory.QuestionCategoryService;
import com.nhattung.wogo.service.questionoption.QuestionOptionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService{

    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final ModelMapper modelMapper;
    private final ServiceCategoryRepository serviceCategoryRepository;
    private final QuestionOptionService questionOptionService;
    @Override
    public QuestionResponseDTO saveQuestion(QuestionRequestDTO requestDTO) {

        QuestionCategory questionCategory = questionCategoryRepository.findById(requestDTO.getQuestionCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_CATEGORY_NOT_FOUND));

        Question savedQuestion = questionRepository.save(
                createQuestion(requestDTO, questionCategory)
        );

        // Save question options if provided
        List<QuestionOptionResponseDTO> questionOptions = new ArrayList<>();
        if (requestDTO.getQuestionOptions() != null && !requestDTO.getQuestionOptions().isEmpty()) {
            requestDTO.getQuestionOptions().forEach(optionRequest -> {
                optionRequest.setQuestionId(savedQuestion.getId());
                QuestionOptionResponseDTO savedOption = questionOptionService.saveOption(optionRequest);
                questionOptions.add(savedOption);
            });
        }

        QuestionResponseDTO responseDTO = convertToResponseDTO(savedQuestion);
        responseDTO.setQuestionOptions(questionOptions);
        return responseDTO;

    }

    private Question createQuestion(QuestionRequestDTO requestDTO, QuestionCategory questionCategory) {
        return Question.builder()
                .questionCategory(questionCategory)
                .questionText(requestDTO.getQuestionText())
                .questionType(requestDTO.getQuestionType())
                .difficultyLevel(requestDTO.getDifficultyLevel())
                .explanation(requestDTO.getExplanation())
                .imageUrl(requestDTO.getImageUrl())
                .isActive(true)
                .build();
    }

    @Override
    public QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        Question updatedQuestion = updateExistingQuestion(existingQuestion, requestDTO);

        return convertToResponseDTO(updatedQuestion);
    }

    private Question updateExistingQuestion(Question existingQuestion, QuestionRequestDTO requestDTO) {

        QuestionCategory questionCategory = questionCategoryRepository.findById(requestDTO.getQuestionCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_CATEGORY_NOT_FOUND));

        existingQuestion.setQuestionText(requestDTO.getQuestionText());
        existingQuestion.setQuestionType(requestDTO.getQuestionType());
        existingQuestion.setDifficultyLevel(requestDTO.getDifficultyLevel());
        existingQuestion.setExplanation(requestDTO.getExplanation());
        existingQuestion.setImageUrl(requestDTO.getImageUrl());
        existingQuestion.setQuestionCategory(questionCategory);
        existingQuestion.setActive(requestDTO.isActive());

        return questionRepository.save(existingQuestion);
    }

    @Override
    public QuestionUpdateResponseDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        QuestionResponseDTO currentData = convertToResponseDTO(question);

        // ServiceCategory options từ DB
        List<OptionResponseDTO> serviceCategoryOptions = serviceCategoryRepository.findAll()
                .stream()
                .map(serviceCategory -> OptionResponseDTO.builder()
                        .value(String.valueOf(serviceCategory.getId()))
                        .label(serviceCategory.getCategoryName())
                        .build())
                .toList();

        // QuestionType options từ enum
        List<OptionResponseDTO> questionTypeOptions = Arrays.stream(QuestionType.values())
                .map(qt -> OptionResponseDTO.builder()
                        .value(qt.name())
                        .label(qt.getType())
                        .build())
                .toList();

        // DifficultyLevel options từ enum
        List<OptionResponseDTO> difficultyLevelOptions = Arrays.stream(DifficultyLevel.values())
                .map(dl -> OptionResponseDTO.builder()
                        .value(dl.name())
                        .label(dl.getLevel())
                        .build())
                .toList();

        return QuestionUpdateResponseDTO.builder()
                .currentQuestion(currentData)
                .serviceCategoriesOptions(serviceCategoryOptions)
                .questionTypeOptions(questionTypeOptions)
                .difficultyLevelOptions(difficultyLevelOptions)
                .build();
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepository.findById(id).ifPresentOrElse(
                questionRepository::delete,
                () -> {
                    throw new AppException(ErrorCode.QUESTION_NOT_FOUND);
                }
        );
    }

    @Override
    public PageResponse<QuestionResponseDTO> getAllQuestions(int page, int size) {
        if(page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Question> questionPage = questionRepository.findAll(pageable);

        List<QuestionResponseDTO> questionResponseList = questionPage.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return PageResponse.<QuestionResponseDTO>builder()
                .currentPage(page)
                .totalPages(questionPage.getTotalPages())
                .totalElements(questionPage.getTotalElements())
                .pageSize(size)
                .data(questionResponseList)
                .build();
    }


    private QuestionResponseDTO convertToResponseDTO(Question question) {
        return modelMapper.map(question, QuestionResponseDTO.class);
    }
}
