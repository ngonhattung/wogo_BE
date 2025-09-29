package com.nhattung.wogo.service.worker.workverification;

import com.nhattung.wogo.dto.request.WorkerVerificationRequestDTO;
import com.nhattung.wogo.entity.ServiceWG;
import com.nhattung.wogo.entity.WorkerVerification;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.VerificationStatus;
import com.nhattung.wogo.enums.VerificationType;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerVerificationRepository;
import com.nhattung.wogo.service.serviceWG.IServiceService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkerVerificationService implements IWorkerVerificationService {

    private final WorkerVerificationRepository workerVerificationRepository;
    private final IUserService userService;
    @Override
    public void saveWorkerVerification(WorkerVerificationRequestDTO request) {
        WorkerVerification workerVerification = createWorkerVerification(request);
        workerVerificationRepository.save(workerVerification);
    }

    private WorkerVerification createWorkerVerification(WorkerVerificationRequestDTO request) {
        return WorkerVerification.builder()
                .verificationType(request.getVerificationType())
                .workerVerificationTest(request.getVerificationTest())
                .verificationStatus(VerificationStatus.PENDING)
                .user(userService.getCurrentUser())
                .service(request.getService())
                .workerDocument(request.getWorkerDocument())
                .build();
    }

    @Override
    public void updateWorkerVerification(Long verificationId, WorkerVerificationRequestDTO request) {
        WorkerVerification existingVerification = workerVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_NOT_FOUND));

        existingVerification.setVerificationStatus(request.getVerificationStatus());
        existingVerification.setRejectionReason(request.getRejectionReason());

        // If the verification is approved, set the approvedAt timestamp
        if (request.getVerificationStatus() == VerificationStatus.APPROVED) {
            existingVerification.setApprovedAt(request.getApprovedAt());
        }


        workerVerificationRepository.save(existingVerification);
    }

    @Override
    public WorkerVerification getWorkerVerificationByWorkerTestId(Long workerTestId) {
        return workerVerificationRepository.findByWorkerVerificationTestId(workerTestId)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_NOT_FOUND));
    }

    @Override
    public List<WorkerVerification> getWorkerVerificationByServiceIdAndUserIdAndType(Long serviceId, VerificationType type) {
        return workerVerificationRepository.findByServiceIdAndUserIdAndVerificationType(serviceId, SecurityUtils.getCurrentUserId(), type);
    }


    @Override
    public WorkerVerification getWorkerVerificationByWorkerDocumentId(Long id) {
        return workerVerificationRepository.findByWorkerDocumentId(id)
                .orElseThrow(() -> new AppException(ErrorCode.VERIFICATION_NOT_FOUND));
    }

}
