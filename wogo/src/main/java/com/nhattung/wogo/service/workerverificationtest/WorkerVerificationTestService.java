package com.nhattung.wogo.service.workerverificationtest;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.request.WorkerVerificationTestRequestDTO;
import com.nhattung.wogo.dto.response.WorkerVerificationTestResponseDTO;
import com.nhattung.wogo.entity.WorkerVerificationTest;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.TestStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerVerificationTestRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WorkerVerificationTestService implements IWorkerVerificationTestService {


    private final WorkerVerificationTestRepository workerVerificationTestRepository;
    private final ModelMapper modelMapper;

    @Override
    public WorkerVerificationTest saveWorkerVerificationTest(WorkerVerificationTestRequestDTO request) {
        return createWorkerVerificationTest(request);
    }

    private WorkerVerificationTest createWorkerVerificationTest(WorkerVerificationTestRequestDTO request) {
        return WorkerVerificationTest.builder()
                .testCode(generateTestCode())
                .questionCategory(request.getQuestionCategory())
                .totalQuestions(WogoConstants.TOTAL_QUESTIONS_TEST)
                .isPassed(false)
                .testStatus(TestStatus.IN_PROGRESS)
                .timeLimitMinutes(WogoConstants.TEST_TIME_LIMIT_MINUTES)
                .passThreshold(WogoConstants.PASSING_SCORE)
                .startedAt(LocalDateTime.now())
                .user(request.getUser())
                .build();
    }

    @Override
    public WorkerVerificationTestResponseDTO updateWorkerVerificationTest(Long testId, WorkerVerificationTestRequestDTO request) {
        WorkerVerificationTest existingTest = workerVerificationTestRepository.findById(testId)
                .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));

        existingTest.setPassed(request.isPassed());
        existingTest.setCorrectAnswers(request.getCorrectAnswers());
        existingTest.setTestStatus(request.getTestStatus());
        existingTest.setCompletedAt(LocalDateTime.now());

        return convertToResponseDTO(
                workerVerificationTestRepository.save(existingTest)
        );
    }

    @Override
    public WorkerVerificationTest getWorkerVerificationTestById(Long testId) {
        return workerVerificationTestRepository.findById(testId)
                .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
    }

    private WorkerVerificationTestResponseDTO convertToResponseDTO(WorkerVerificationTest workerVerificationTest) {
        return modelMapper.map(workerVerificationTest, WorkerVerificationTestResponseDTO.class);
    }

    private String generateTestCode() {
        // Logic to generate a unique test code
        return "Worker-TEST" + LocalDate.now().getYear() + "-" + System.currentTimeMillis();
    }
}
