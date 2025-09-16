package com.nhattung.wogo.dto.request;


import lombok.Data;

@Data
public class AddressRequestDTO {
    private Double latitude;
    private Double longitude;
    private String role; // USER,WORKER
    private Long userId;
}
