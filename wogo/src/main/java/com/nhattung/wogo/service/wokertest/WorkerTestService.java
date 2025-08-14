package com.nhattung.wogo.service.wokertest;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.request.*;
import com.nhattung.wogo.dto.response.*;
import com.nhattung.wogo.entity.QuestionCategory;
import com.nhattung.wogo.entity.User;
import com.nhattung.wogo.entity.WorkerVerification;
import com.nhattung.wogo.entity.WorkerVerificationTest;
import com.nhattung.wogo.enums.*;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.QuestionOptionRepository;
import com.nhattung.wogo.service.question.IQuestionService;
import com.nhattung.wogo.service.questioncategory.IQuestionCategoryService;
import com.nhattung.wogo.service.testanswer.ITestAnswerService;
import com.nhattung.wogo.service.user.IUserService;
import com.nhattung.wogo.service.worker.IWorkerService;
import com.nhattung.wogo.service.workerservice.IWorkerServiceService;
import com.nhattung.wogo.service.workerverification.IWorkerVerificationService;
import com.nhattung.wogo.service.workerverificationtest.IWorkerVerificationTestService;
import com.nhattung.wogo.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkerTestService implements IWorkerTestService{

    private final IWorkerVerificationService workerVerificationService;
    private final IWorkerVerificationTestService workerVerificationTestService;
    private final IQuestionCategoryService questionCategoryService;
    private final IUserService userService;
    private final IQuestionService questionService;
    private final QuestionOptionRepository questionOptionRepository;
    private final IWorkerService workerService;
    private final IWorkerServiceService workerServiceService;
    private final ITestAnswerService testAnswerService;
    @Override
    public CreateTestResponseDTO createWorkerTest(WorkerTestRequestDTO request) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        // Kiểm tra nếu người dùng đã là worker và đã đăng ký nghiệp vụ này
        if (workerService.getWorkerByUserId(currentUserId) != null &&
                workerServiceService.checkWorkerServiceExists(currentUserId, request.getServiceId())) {
            throw new AppException(ErrorCode.WORKER_SERVICE_EXISTS);
        }

        // Lấy thông tin danh mục câu hỏi và người dùng
        QuestionCategory questionCategory = questionCategoryService.getCategoryEntityByServiceId(request.getServiceId());
        User user = userService.getUserByIdEntity(currentUserId);

        // Tạo bài test
        WorkerVerificationTest workerVerificationTest = workerVerificationTestService.saveWorkerVerificationTest(
                WorkerVerificationTestRequestDTO.builder()
                        .user(user)
                        .questionCategory(questionCategory)
                        .build()
        );

        // Tạo xác minh worker tương ứng
        WorkerVerificationResponseDTO workerVerification = workerVerificationService.saveWorkerVerification(
                WorkerVerificationRequestDTO.builder()
                        .user(user)
                        .verificationTest(workerVerificationTest)
                        .verificationType(VerificationType.TEST)
                        .build()
        );

        // Lấy danh sách câu hỏi ngẫu nhiên theo danh mục
        List<QuestionResponseDTO> randomQuestions = questionService.findRandomQuestions(questionCategory.getId());

        return CreateTestResponseDTO.builder()
                .workerVerification(workerVerification)
                .questions(randomQuestions)
                .build();
    }


    @Override
    public CompleteTestResponseDTO completeWorkerTest(SubmitTestRequestDTO request) {
        Long testId = request.getTestId();
        WorkerVerificationTest workerVerificationTest = workerVerificationTestService.getWorkerVerificationTestById(testId);
        WorkerVerification workerVerification = workerVerificationService.getWorkerVerificationByWorkerTestId(testId);

        int correctAnswers = calculateCorrectAndAddHistoryAnswers(request.getAnswers(), workerVerificationTest);
        double scorePercentage = (correctAnswers * 100.0) / workerVerificationTest.getTotalQuestions();
        boolean isPassed = scorePercentage >= workerVerificationTest.getPassThreshold();

        // Cập nhật WorkerVerificationTest
        workerVerificationTest.setPassed(isPassed);
        workerVerificationTest.setCorrectAnswers(correctAnswers);
        workerVerificationTest.setTestStatus(isPassed ? TestStatus.PASSED : TestStatus.FAILED);

        // Cập nhật WorkerVerification
        workerVerification.setVerificationStatus(isPassed ? VerificationStatus.APPROVED : VerificationStatus.REJECTED);
        workerVerification.setDocumentVerified(false);
        workerVerification.setRejectionReason(isPassed ? null : WogoConstants.TEST_REJECTION_REASON);

        WorkerResponseDTO workerResponseDTO = null;

        if (isPassed) {
            userService.addUserRole(workerVerification.getUser().getId(), ROLE.WORKER.name());
            workerResponseDTO = workerService.saveWorker(
                    WorkerRequestDTO.builder().user(workerVerification.getUser()).build()
            );
            workerServiceService.saveWorkerService(
                    workerVerification.getUser().getWorker(),
                    workerVerificationTest.getQuestionCategory().getService()
            );
        }

        // Gọi cập nhật thông tin test và verification
        workerVerificationTestService.updateWorkerVerificationTest(
                testId,
                WorkerVerificationTestRequestDTO.builder()
                        .isPassed(isPassed)
                        .correctAnswers(correctAnswers)
                        .testStatus(workerVerificationTest.getTestStatus())
                        .build()
        );

        WorkerVerificationResponseDTO workerVerificationResponseDTO = workerVerificationService.updateWorkerVerification(
                workerVerification.getId(),
                WorkerVerificationRequestDTO.builder()
                        .verificationStatus(workerVerification.getVerificationStatus())
                        .documentVerified(false)
                        .rejectionReason(workerVerification.getRejectionReason())
                        .build()
        );

        return CompleteTestResponseDTO.builder()
                .workerVerification(workerVerificationResponseDTO)
                .worker(workerResponseDTO)
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
