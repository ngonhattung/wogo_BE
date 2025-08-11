package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.QuestionOptionRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.QuestionOptionResponseDTO;
import com.nhattung.wogo.service.questionoption.IQuestionOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/question-options")
public class QuestionOptionController {

    private final IQuestionOptionService questionOptionService;


    @GetMapping("/all")
    public ApiResponse<PageResponse<QuestionOptionResponseDTO>> getAllOptions(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ApiResponse.<PageResponse<QuestionOptionResponseDTO>>builder()
                .result(questionOptionService.getAllOptions(page, size))
                .message("Successfully retrieved all question options")
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionOptionResponseDTO> getOptionById(@PathVariable Long id) {
        return ApiResponse.<QuestionOptionResponseDTO>builder()
                .result(questionOptionService.getOptionById(id))
                .message("Successfully retrieved option with ID: " + id)
                .build();
    }

    @GetMapping("/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<Void> deleteOption(@RequestParam Long id) {
        questionOptionService.deleteOption(id);
        return ApiResponse.<Void>builder()
                .message("Successfully deleted option with ID: " + id)
                .build();
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<QuestionOptionResponseDTO> updateOption(@PathVariable Long id,
                                                               @RequestBody QuestionOptionRequestDTO request) {
        return ApiResponse.<QuestionOptionResponseDTO>builder()
                .result(questionOptionService.updateOption(id, request))
                .message("Successfully updated option with ID: " + id)
                .build();
    }
}
