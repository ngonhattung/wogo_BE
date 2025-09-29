package com.nhattung.wogo.dto.response;

import lombok.*;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobResponseDTO extends JobBaseResponseDTO {
    private List<SendQuotedResponseDTO> workerQuotes;
}
