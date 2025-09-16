package com.nhattung.wogo.service.address;

import com.nhattung.wogo.dto.request.AddressRequestDTO;
import com.nhattung.wogo.entity.Address;

public interface IAddressService {
        void saveOrUpdateAddress(AddressRequestDTO request);
        Address findByUserId(Long userId);
        Address findByWorkerId(Long workerId);
}
