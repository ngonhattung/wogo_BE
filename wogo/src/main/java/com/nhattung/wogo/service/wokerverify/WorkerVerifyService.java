package com.nhattung.wogo.service.wokerverify;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.*;
import com.nhattung.wogo.enums.*;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.QuestionOptionRepository;
import com.nhattung.wogo.service.question.IQuestionService;
import com.nhattung.wogo.service.questioncategory.IQuestionCategoryService;
import com.nhattung.wogo.service.testanswer.ITestAnswerService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.service.walletexpense.IWorkerWalletExpenseService;
import com.nhattung.wogo.service.walletrevenue.IWorkerWalletRevenueService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.service.workerdocument.IWorkerDocumentService;
import com.nhattung.wogo.service.workerservice.IWorkerServiceService;
import com.nhattung.wogo.service.workerverification.IWorkerVerificationService;
import com.nhattung.wogo.service.workerverificationtest.IWorkerVerificationTestService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkerVerifyService implements IWorkerVerifyService {

    private final IWorkerVerificationService workerVerificationService;
    private final IWorkerVerificationTestService workerVerificationTestService;
    private final IQuestionCategoryService questionCategoryService;
    private final IUserService userService;
    private final IQuestionService questionService;
    private final QuestionOptionRepository questionOptionRepository;
    private final IWorkerService workerService;
    private final IWorkerServiceService workerServiceService;
    private final ITestAnswerService testAnswerService;
    private final IWorkerDocumentService workerDocumentService;
    private final IWorkerWalletExpenseService workerWalletExpenseService;
    private final IWorkerWalletRevenueService workerWalletRevenueService;

    @Override
    @Transactional
    public CreateTestResponseDTO createWorkerTest(WorkerTestRequestDTO request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // Kiểm tra nếu người dùng đã là worker và đã đăng ký nghiệp vụ này
        if (checkWorkerServiceExists(currentUserId, request.getServiceId())) {
            throw new AppException(ErrorCode.WORKER_SERVICE_EXISTS);
        }

        // Lấy thông tin danh mục câu hỏi và người dùng
        QuestionCategory questionCategory = questionCategoryService.getCategoryEntityByServiceId(request.getServiceId());
        User user = userService.getUserByIdEntity(currentUserId);

        // Tạo bài test
        WorkerVerificationTest workerVerificationTest = workerVerificationTestService.saveWorkerVerificationTest(
                WorkerVerificationTestRequestDTO.builder()
                        .questionCategory(questionCategory)
                        .build()
        );

        // Tạo xác minh worker tương ứng
        workerVerificationService.saveWorkerVerification(
                WorkerVerificationRequestDTO.builder()
                        .user(user)
                        .verificationTest(workerVerificationTest)
                        .verificationType(VerificationType.TEST)
                        .serviceId(request.getServiceId())
                        .build()
        );

        // Lấy danh sách câu hỏi ngẫu nhiên theo danh mục
        List<QuestionResponseDTO> randomQuestions = questionService.findRandomQuestions(questionCategory.getId());

        return CreateTestResponseDTO.builder()
                .questions(randomQuestions)
                .testId(workerVerificationTest.getId())
                .build();
    }


    @Override
    @Transactional
    public CompleteTestResponseDTO completeWorkerTest(SubmitTestRequestDTO request) {
        Long testId = request.getTestId();

        WorkerVerificationTest workerVerificationTest = workerVerificationTestService.getWorkerVerificationTestById(testId);
        WorkerVerification workerVerification = workerVerificationService.getWorkerVerificationByWorkerTestId(testId);

        int correctAnswers = calculateCorrectAndAddHistoryAnswers(request.getAnswers(), workerVerificationTest);
        double scorePercentage = (correctAnswers * 100.0) / workerVerificationTest.getTotalQuestions();
        boolean isPassed = scorePercentage >= workerVerificationTest.getPassThreshold();

        workerVerificationTest.setPassed(isPassed);
        workerVerificationTest.setCorrectAnswers(correctAnswers);
        workerVerificationTest.setTestStatus(isPassed ? TestStatus.PASSED : TestStatus.FAILED);

        workerVerification.setVerificationStatus(isPassed ? VerificationStatus.APPROVED : VerificationStatus.REJECTED);
        workerVerification.setRejectionReason(isPassed ? null : WogoConstants.TEST_REJECTION_REASON);
        workerVerification.setApprovedAt(isPassed ? LocalDateTime.now() : null);

        if (isPassed) {
            ensureUserHasWorkerRole(workerVerification.getUser().getId());
            ensureWorkerAndServiceAndWallet(workerVerification.getUser(), workerVerificationTest.getQuestionCategory().getService());
        }

        workerVerificationTestService.updateWorkerVerificationTest(
                testId,
                buildWorkerTestUpdateDTO(workerVerificationTest, isPassed, correctAnswers)
        );

        workerVerificationService.updateWorkerVerification(
                workerVerification.getId(),
                buildWorkerVerificationUpdateDTO(workerVerification)
        );

        return CompleteTestResponseDTO.builder()
                .isPassed(isPassed)
                .scorePercentage(scorePercentage)
                .build();
    }

    @Override
    @Transactional
    public WorkerDocumentResponseDTO uploadWorkerDocument(WorkerDocumentRequestDTO request, List<MultipartFile> files) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        if (checkWorkerServiceExists(currentUserId, request.getServiceId())) {
            throw new AppException(ErrorCode.WORKER_SERVICE_EXISTS);
        }

        List<WorkerVerification> existingVerifications = workerVerificationService
                .getWorkerVerificationByServiceIdAndUserIdAndType(
                        request.getServiceId(),
                        currentUserId,
                        VerificationType.DOCUMENT
                );

        if( existingVerifications != null && !existingVerifications.isEmpty()) {
            throw new AppException(ErrorCode.WORKER_DOCUMENT_EXISTS);
        }

        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.FILE_UPLOAD_ERROR_EMPTY);
        }

        User user = userService.getUserByIdEntity(currentUserId);

        WorkerDocument workerDocument = workerDocumentService.saveWorkerDocument(
                WorkerDocumentRequestDTO.builder()
                        .documentType(request.getDocumentType())
                        .documentName(request.getDocumentName())
                        .verificationStatus(VerificationStatus.PENDING)
                        .serviceId(request.getServiceId())
                        .build(),
                files
        );

        workerVerificationService.saveWorkerVerification(
                WorkerVerificationRequestDTO.builder()
                        .user(user)
                        .verificationType(VerificationType.DOCUMENT)
                        .verificationStatus(VerificationStatus.PENDING)
                        .serviceId(request.getServiceId())
                        .workerDocument(workerDocument)
                        .build()
        );

        return workerDocumentService.convertToResponseDTO(workerDocument);
    }

    @Override
    @Transactional
    public WorkerDocumentResponseDTO updateVerifyStatusByDocument(WorkerDocumentRequestDTO request) {
        VerificationStatus status = request.getVerificationStatus();
        if (status == VerificationStatus.APPROVED || status == VerificationStatus.REJECTED) {
            return processWorkerDocumentVerification(request);
        }
        return null;
    }


    private boolean checkWorkerServiceExists(Long currentUserId, Long serviceId) {
        return workerServiceService.checkWorkerServiceExists(currentUserId, serviceId);
    }

    private WorkerDocumentResponseDTO processWorkerDocumentVerification(WorkerDocumentRequestDTO request) {
        VerificationStatus status = request.getVerificationStatus();

        // Get WorkerVerification by document ID
        WorkerVerification workerVerification = workerVerificationService.getWorkerVerificationByWorkerDocumentId(request.getId());

        // Update WorkerVerification status
        updateWorkerVerificationStatus(workerVerification.getId(), status);

        if (status == VerificationStatus.APPROVED) {
            ensureUserHasWorkerRole(workerVerification.getUser().getId());
            ensureWorkerAndServiceAndWallet(workerVerification.getUser(), workerVerification.getService());
        }

        // Update and return WorkerDocument
        return workerDocumentService.updateWorkerDocument(
                WorkerDocumentRequestDTO.builder()
                        .id(request.getId())
                        .verificationStatus(status)
                        .build()
        );
    }

    private void updateWorkerVerificationStatus(Long verificationId, VerificationStatus status) {
        workerVerificationService.updateWorkerVerification(
                verificationId,
                WorkerVerificationRequestDTO.builder()
                        .verificationType(VerificationType.DOCUMENT)
                        .verificationStatus(status)
                        .build()
        );
    }

    private void ensureWorkerAndServiceAndWallet(User user, ServiceWG service) {
        Worker worker = workerService.isWorkerExists(user.getId())
                ? workerService.getWorkerByUserId(user.getId())
                : workerService.saveWorker(WorkerRequestDTO.builder().user(user).build());

        workerServiceService.saveWorkerService(worker, service);

        //check xem có ví chưa
        if(!workerWalletExpenseService.checkExistWalletByWorkerId(worker.getId()) && !workerWalletRevenueService.checkExistWalletByWorkerId(worker.getId())){
            workerWalletExpenseService.saveWorkerWalletExpense(
                    WorkerWalletExpenseRequestDTO.builder()
                            .worker(worker)
                            .totalExpense(WogoConstants.INITIAL_WALLET_BALANCE)
                            .expenseBalance(WogoConstants.INITIAL_WALLET_BALANCE)
                            .isActive(true)
                            .build()
            );

            workerWalletRevenueService.saveWorkerWalletRevenue(
                    WorkerWalletRevenueRequestDTO.builder()
                            .worker(worker)
                            .totalRevenue(WogoConstants.INITIAL_WALLET_BALANCE)
                            .revenueBalance(WogoConstants.INITIAL_WALLET_BALANCE)
                            .isActive(true)
                            .build()
            );
        }
    }

    private void ensureUserHasWorkerRole(Long userId) {
        if (!userService.isExistRole(userId, ROLE.WORKER.getValue())) {
            userService.addUserRole(userId, ROLE.WORKER.getValue());
        }
    }

    private WorkerVerificationTestRequestDTO buildWorkerTestUpdateDTO(
            WorkerVerificationTest test, boolean isPassed, int correctAnswers) {
        return WorkerVerificationTestRequestDTO.builder()
                .isPassed(isPassed)
                .correctAnswers(correctAnswers)
                .testStatus(test.getTestStatus())
                .build();
    }

    private WorkerVerificationRequestDTO buildWorkerVerificationUpdateDTO(
            WorkerVerification verification) {
        return WorkerVerificationRequestDTO.builder()
                .verificationStatus(verification.getVerificationStatus())
                .rejectionReason(verification.getRejectionReason())
                .approvedAt(verification.getApprovedAt())
                .build();
    }

    private int calculateCorrectAndAddHistoryAnswers(
            List<SubmitTestRequestDTO.AnswerRequestDTO> answers,
            WorkerVerificationTest workerVerificationTest) {

        int correctAnswers = 0;

        for (SubmitTestRequestDTO.AnswerRequestDTO answer : answers) {
            Set<Long> correctSet = new HashSet<>(
                    questionOptionRepository.findCorrectOptionIdsByQuestionId(answer.getQuestionId())
            );
            Set<Long> userSet = new HashSet<>(answer.getSelectedOptionIds());

            // Đúng cả câu khi tập hợp bằng nhau
            boolean isWholeQuestionCorrect = correctSet.equals(userSet);
            if (isWholeQuestionCorrect) {
                correctAnswers++;
            }

            // Lưu từng lựa chọn user đã chọn
            for (Long selectedOptionId : answer.getSelectedOptionIds()) {
                boolean isOptionCorrect = correctSet.contains(selectedOptionId);

                testAnswerService.saveTestAnswer(TestAnswerRequestDTO.builder()
                        .questionId(answer.getQuestionId())
                        .questionOptionId(selectedOptionId)
                        .isCorrect(isOptionCorrect) // đúng/sai theo từng option
                        .workerTest(workerVerificationTest)
                        .build());
            }
        }
        return correctAnswers;
    }



}
