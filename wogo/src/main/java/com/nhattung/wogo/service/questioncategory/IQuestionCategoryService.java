package com.nhattung.wogo.service.questioncategory;

import com.nhattung.wogo.dto.request.QuestionCategoryRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionCategoryResponseDTO;
import com.nhattung.wogo.dto.response.QuestionCategoryUpdateResponseDTO;

public interface IQuestionCategoryService {

    //CRUD + Search
    QuestionCategoryResponseDTO saveCategory(QuestionCategoryRequestDTO questionCategory);
    QuestionCategoryResponseDTO updateCategory(Long id, QuestionCategoryRequestDTO questionCategory);
    public QuestionCategoryUpdateResponseDTO getCategoryById(Long id);
    void deleteCategory(Long id);
    PageResponse<QuestionCategoryResponseDTO> getAllCategories(int page, int size);

}
