package com.nhattung.wogo.service.user.address;

import com.nhattung.wogo.dto.request.AddressRequestDTO;
import com.nhattung.wogo.entity.Address;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.ROLE;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.AddressRepository;
import com.nhattung.wogo.repository.UserRepository;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService{

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public void saveOrUpdateAddress(AddressRequestDTO request) {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra có address nào cùng role không
        Optional<Address> existingAddressOpt = user.getAddresses().stream()
                .filter(addr -> addr.getRole().equalsIgnoreCase(request.getRole()))
                .findFirst();

        Address address;
        if (existingAddressOpt.isPresent()) {
            // Update
            address = existingAddressOpt.get();
            address.setLatitude(request.getLatitude());
            address.setLongitude(request.getLongitude());
        } else {
            // Thêm mới
            address = new Address();
            address.setLatitude(request.getLatitude());
            address.setLongitude(request.getLongitude());
            address.setRole(request.getRole());
            address.setUser(user);
            user.getAddresses().add(address); // nếu muốn đồng bộ 2 chiều
        }

        addressRepository.save(address);
    }

    @Override
    public Address findByWorkerId(Long workerId) {
        return addressRepository.findByUserIdAndRole(workerId, ROLE.WORKER.name())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
    }
}
