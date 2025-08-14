package com.nhattung.wogo.service.workerverification;

import com.nhattung.wogo.dto.request.WorkerVerificationRequestDTO;
import com.nhattung.wogo.dto.response.WorkerVerificationResponseDTO;
import com.nhattung.wogo.entity.WorkerVerification;
import com.nhattung.wogo.entity.WorkerVerificationTest;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.VerificationStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerVerificationRepository;
import com.nhattung.wogo.repository.WorkerVerificationTestRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkerVerificationService implements IWorkerVerificationService{

    private final WorkerVerificationRepository workerVerificationRepository;
    private final WorkerVerificationTestRepository workerVerificationTestRepository;
    private final ModelMapper modelMapper;
    @Override
    public WorkerVerificationResponseDTO saveWorkerVerification(WorkerVerificationRequestDTO request) {

        WorkerVerification workerVerification = createWorkerVerification(request);

        return convertToResponseDTO(
                workerVerificationRepository.save(workerVerification)
        );
    }

    private WorkerVerification createWorkerVerification(WorkerVerificationRequestDTO request) {
        return WorkerVerification.builder()
                .verificationType(request.getVerificationType())
                .documentVerified(false)
                .workerVerificationTest(request.getVerificationTest())
                .verificationStatus(VerificationStatus.PENDING)
                .user(request.getUser())
                .build();
    }

    @Override
    public WorkerVerificationResponseDTO updateWorkerVerification(Long verificationId, WorkerVerificationRequestDTO request) {
        WorkerVerification existingVerification = workerVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_NOT_FOUND));

        existingVerification.setDocumentVerified(request.isDocumentVerified());
        existingVerification.setVerificationStatus(request.getVerificationStatus());
        existingVerification.setRejectionReason(request.getRejectionReason());

        // If the verification is approved, set the approvedAt timestamp
        if (request.getVerificationStatus() == VerificationStatus.APPROVED) {
            existingVerification.setApprovedAt(request.getApprovedAt());
        }

        return convertToResponseDTO(
                workerVerificationRepository.save(existingVerification)
        );
    }

    @Override
    public WorkerVerification getWorkerVerificationByWorkerTestId(Long workerTestId) {
        return workerVerificationRepository.findByWorkerVerificationTestId(workerTestId)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_NOT_FOUND));
    }


    private WorkerVerificationResponseDTO convertToResponseDTO(WorkerVerification workerVerification) {
        return modelMapper.map(workerVerification, WorkerVerificationResponseDTO.class);
        }
}
