package com.nhattung.wogo.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateTestResponseDTO {
    private List<QuestionResponseDTO> questions;
}
