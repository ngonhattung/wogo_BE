package com.nhattung.wogo.dto.response;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobSummaryResponseDTO extends JobBaseResponseDTO{
    private double distance;
}
