package com.nhattung.wogo.controller;

import com.nhattung.wogo.dto.request.AddressRequestDTO;
import com.nhattung.wogo.dto.response.ApiResponse;
import com.nhattung.wogo.service.user.address.IAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final IAddressService addressService;

    @PostMapping("/save-or-update")
    public ApiResponse<Void> saveOrUpdateAddress(@RequestBody AddressRequestDTO request) {
        addressService.saveOrUpdateAddress(request);
        return ApiResponse.<Void>builder()
                .message("Address saved or updated successfully")
                .build();
    }


    //hoặc chia 2 api riêng biệt

}
