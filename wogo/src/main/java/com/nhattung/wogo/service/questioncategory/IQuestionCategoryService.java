package com.nhattung.wogo.service.questioncategory;

import com.nhattung.wogo.dto.request.QuestionCategoryRequestDTO;
import com.nhattung.wogo.dto.response.QuestionCategoryResponseDTO;

public interface IQuestionCategoryService {

    //CRUD + Search
    QuestionCategoryResponseDTO saveCategory(QuestionCategoryRequestDTO questionCategory);

}
