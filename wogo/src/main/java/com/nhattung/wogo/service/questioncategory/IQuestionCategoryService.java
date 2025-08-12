package com.nhattung.wogo.service.questioncategory;

import com.nhattung.wogo.dto.request.QuestionCategoryRequestDTO;
import com.nhattung.wogo.dto.response.OptionResponseDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionCategoryResponseDTO;
import com.nhattung.wogo.dto.response.QuestionCategoryUpdateResponseDTO;
import com.nhattung.wogo.entity.QuestionCategory;

import java.util.List;

public interface IQuestionCategoryService {

    QuestionCategoryResponseDTO saveCategory(QuestionCategoryRequestDTO questionCategory);
    QuestionCategoryResponseDTO updateCategory(Long id, QuestionCategoryRequestDTO questionCategory);
    QuestionCategoryUpdateResponseDTO getCategoryById(Long id);
    QuestionCategory getCategoryEntityById(Long id);
    void deleteCategory(Long id);
    List<OptionResponseDTO> getAllCategoriesOptions();
    PageResponse<QuestionCategoryResponseDTO> getAllCategories(int page, int size);

}
