package com.nhattung.wogo.service.questioncategory;

import com.nhattung.wogo.dto.request.QuestionCategoryRequestDTO;
import com.nhattung.wogo.dto.response.QuestionCategoryResponseDTO;
import com.nhattung.wogo.entity.QuestionCategory;
import com.nhattung.wogo.entity.ServiceCategory;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.QuestionCategoryRepository;
import com.nhattung.wogo.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionCategoryService implements IQuestionCategoryService{

    private final QuestionCategoryRepository questionCategoryRepository;
    private final ServiceCategoryRepository serviceCategoryService;


    @Override
    public QuestionCategoryResponseDTO saveCategory(QuestionCategoryRequestDTO questionCategory) {
        ServiceCategory serviceCategory = serviceCategoryService.findByCategoryName(questionCategory.getCategoryServiceName())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND));

        QuestionCategory savedQuestionCategory = questionCategoryRepository.save(createQuestionCategory(questionCategory, serviceCategory));

        return QuestionCategoryResponseDTO.builder()
                .id(savedQuestionCategory.getId())
                .categoryService(savedQuestionCategory.getServiceCategory())
                .requiredScore(savedQuestionCategory.getRequiredScore())
                .totalQuestion(savedQuestionCategory.getTotalQuestions())
                .questionPerTest(savedQuestionCategory.getQuestionsPerTest())
                .description(savedQuestionCategory.getDescription())
                .build();
    }

    private QuestionCategory createQuestionCategory(QuestionCategoryRequestDTO request, ServiceCategory serviceCategory) {
        return QuestionCategory.builder()
                .serviceCategory(serviceCategory)
                .requiredScore(request.getRequiredScore())
                .totalQuestions(request.getTotalQuestion())
                .questionsPerTest(request.getQuestionPerTest())
                .description(request.getDescription())
                .isActive(true) // Assuming new categories are active by default
                .build();
    }
}
