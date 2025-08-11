package com.nhattung.wogo.service.questioncategory;

import com.nhattung.wogo.dto.request.QuestionCategoryRequestDTO;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.QuestionCategory;
import com.nhattung.wogo.entity.ServiceCategory;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.QuestionCategoryRepository;
import com.nhattung.wogo.repository.ServiceCategoryRepository;
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
public class QuestionCategoryService implements IQuestionCategoryService{

    private final QuestionCategoryRepository questionCategoryRepository;
    private final ServiceCategoryRepository serviceCategoryService;
    private final ModelMapper modelMapper;
    private final ServiceCategoryRepository serviceCategoryRepository;
    @Override
    public QuestionCategoryResponseDTO saveCategory(QuestionCategoryRequestDTO questionCategory) {
        ServiceCategory serviceCategory = serviceCategoryService.findById(questionCategory.getCategoryServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND));

        QuestionCategory savedQuestionCategory = questionCategoryRepository.save(createQuestionCategory(questionCategory, serviceCategory));

        return convertToResponseDTO(savedQuestionCategory);
    }
    private QuestionCategory createQuestionCategory(QuestionCategoryRequestDTO request, ServiceCategory serviceCategory) {
        return QuestionCategory.builder()
                .serviceCategory(serviceCategory)
                .requiredScore(request.getRequiredScore())
                .questionsPerTest(request.getQuestionPerTest())
                .description(request.getDescription())
                .isActive(true) // Assuming new categories are active by default
                .build();
    }

    @Override
    public QuestionCategoryResponseDTO updateCategory(Long id, QuestionCategoryRequestDTO questionCategory) {
        QuestionCategory existingCategory = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_CATEGORY_NOT_FOUND));

        QuestionCategory updatedCategory = updateExistingCategory(existingCategory, questionCategory);

        return convertToResponseDTO(updatedCategory);
    }

    public QuestionCategory updateExistingCategory(QuestionCategory existingCategory, QuestionCategoryRequestDTO request) {
        ServiceCategory serviceCategory = serviceCategoryService.findById(request.getCategoryServiceId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_CATEGORY_NOT_FOUND));

        existingCategory.setServiceCategory(serviceCategory);
        existingCategory.setRequiredScore(request.getRequiredScore());
        existingCategory.setQuestionsPerTest(request.getQuestionPerTest());
        existingCategory.setDescription(request.getDescription());
        existingCategory.setActive(request.isActive());

        return questionCategoryRepository.save(existingCategory);
    }

    @Override
    public QuestionCategoryUpdateResponseDTO getCategoryById(Long id) {
        QuestionCategory questionCategory = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_CATEGORY_NOT_FOUND));

        QuestionCategoryResponseDTO currentCategory = convertToResponseDTO(questionCategory);

        // ServiceCategory options từ DB
        List<OptionResponseDTO> serviceCategoryOptions = serviceCategoryRepository.findByParentIdIsNull()
                .stream()
                .map(serviceCategory -> OptionResponseDTO.builder()
                        .value(String.valueOf(serviceCategory.getId()))
                        .label(serviceCategory.getCategoryName())
                        .build())
                .toList();

        return QuestionCategoryUpdateResponseDTO.builder()
                .currentQuestionCategory(currentCategory)
                .serviceCategoriesOptions(serviceCategoryOptions)
                .build();


    }

    @Override
    public void deleteCategory(Long id) {
        questionCategoryRepository.findById(id).ifPresentOrElse(
                questionCategoryRepository::delete,
                () -> {
                    throw new AppException(ErrorCode.QUESTION_CATEGORY_NOT_FOUND);
                }
        );
    }

    @Override
    public PageResponse<QuestionCategoryResponseDTO> getAllCategories(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<QuestionCategory> questionCategoryPage = questionCategoryRepository.findAll(pageable);
        List<QuestionCategoryResponseDTO> questionCategoryResponseList = questionCategoryPage.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return PageResponse.<QuestionCategoryResponseDTO>builder()
                .currentPage(page)
                .totalPages(questionCategoryPage.getTotalPages())
                .totalElements(questionCategoryPage.getTotalElements())
                .pageSize(questionCategoryPage.getSize())
                .data(questionCategoryResponseList)
                .build();
    }

    private QuestionCategoryResponseDTO convertToResponseDTO(QuestionCategory questionCategory) {
        return modelMapper.map(questionCategory, QuestionCategoryResponseDTO.class);
    }

}
