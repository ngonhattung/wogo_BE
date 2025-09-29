package com.nhattung.wogo.service.user.address;

import com.nhattung.wogo.dto.request.AddressRequestDTO;
import com.nhattung.wogo.entity.Address;

public interface IAddressService {
        void saveOrUpdateAddress(AddressRequestDTO request);
        Address findByWorkerId(Long workerId);
}
