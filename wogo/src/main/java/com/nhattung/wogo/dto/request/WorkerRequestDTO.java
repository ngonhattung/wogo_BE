package com.nhattung.wogo.dto.request;

import com.nhattung.wogo.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerRequestDTO {
    private User user;
}
