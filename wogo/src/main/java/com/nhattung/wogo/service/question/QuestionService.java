package com.nhattung.wogo.service.question;

import com.nhattung.wogo.dto.request.QuestionRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.Question;
import com.nhattung.wogo.entity.QuestionCategory;
import com.nhattung.wogo.enums.DifficultyLevel;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.QuestionType;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.QuestionRepository;
import com.nhattung.wogo.service.question.questioncategory.IQuestionCategoryService;
import com.nhattung.wogo.service.question.questionoption.QuestionOptionService;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService{

    private final QuestionRepository questionRepository;
    private final IQuestionCategoryService questionCategoryService;
    private final ModelMapper modelMapper;
    private final QuestionOptionService questionOptionService;
    private final UploadToS3 uploadToS3;

    @Override
    public QuestionResponseDTO saveQuestion(QuestionRequestDTO request, MultipartFile imageFile) {

        QuestionCategory questionCategory = questionCategoryService.getCategoryEntityById(request.getQuestionCategoryId());

        Question savedQuestion = questionRepository.save(
                createQuestion(request, questionCategory,imageFile)
        );

        // Save question options if provided
        List<QuestionOptionResponseDTO> questionOptions = new ArrayList<>();
        if (request.getQuestionOptions() != null && !request.getQuestionOptions().isEmpty()) {
            request.getQuestionOptions().forEach(optionRequest -> {
                optionRequest.setQuestionId(savedQuestion.getId());
                QuestionOptionResponseDTO savedOption = questionOptionService.saveOption(optionRequest);
                questionOptions.add(savedOption);
            });
        }

        QuestionResponseDTO responseDTO = convertToResponseDTO(savedQuestion);
        responseDTO.setQuestionOptions(questionOptions);
        return responseDTO;

    }

    private Question createQuestion(QuestionRequestDTO request, QuestionCategory questionCategory, MultipartFile imageFile) {

        String imageUrl = uploadToS3.handleImageUpload(imageFile);

        return Question.builder()
                .questionCategory(questionCategory)
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .difficultyLevel(request.getDifficultyLevel())
                .explanation(request.getExplanation())
                .imageUrl(imageUrl)
                .isActive(true)
                .build();
    }

    @Override
    public QuestionResponseDTO updateQuestion(Long id, QuestionRequestDTO requestDTO, MultipartFile imageFile) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        Question updatedQuestion = updateExistingQuestion(existingQuestion, requestDTO, imageFile);

        return convertToResponseDTO(updatedQuestion);
    }

    private Question updateExistingQuestion(Question existingQuestion, QuestionRequestDTO request, MultipartFile imageFile) {

        QuestionCategory category = questionCategoryService.getCategoryEntityById(request.getQuestionCategoryId());

        String imageUrl = uploadToS3.handleImageUpload(imageFile);

        existingQuestion.setQuestionText(request.getQuestionText());
        existingQuestion.setQuestionType(request.getQuestionType());
        existingQuestion.setDifficultyLevel(request.getDifficultyLevel());
        existingQuestion.setExplanation(request.getExplanation());
        existingQuestion.setImageUrl(imageUrl);
        existingQuestion.setQuestionCategory(category);
        existingQuestion.setActive(request.getIsActive());

        return questionRepository.save(existingQuestion);
    }

    @Override
    public QuestionUpdateResponseDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        QuestionResponseDTO currentData = convertToResponseDTO(question);

        // QuestionCategory options từ DB
        List<OptionResponseDTO> serviceCategoryOptions = questionCategoryService.getAllCategoriesOptions();

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
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
        questionRepository.delete(question);
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

    @Override
    public List<QuestionResponseDTO> findRandomQuestions(Long categoryId) {
        List<Question> easy = getRandomQuestions(categoryId, "EASY", 10);
        List<Question> medium = getRandomQuestions(categoryId, "MEDIUM", 7);
        List<Question> hard = getRandomQuestions(categoryId, "HARD", 3);

        List<Question> allQuestions = new ArrayList<>();
        allQuestions.addAll(easy);
        allQuestions.addAll(medium);
        allQuestions.addAll(hard);

        return allQuestions.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    private List<Question> getRandomQuestions(Long categoryId, String level, int needed) {
        // Lấy gấp đôi để tăng tính random, rồi shuffle
        List<Question> questions = questionRepository.findRandomByDifficulty(
                categoryId, level, needed * 2
        );
        Collections.shuffle(questions);
        return questions.stream().limit(needed).toList();
    }


    private QuestionResponseDTO convertToResponseDTO(Question question) {
        return modelMapper.map(question, QuestionResponseDTO.class);
    }
}
