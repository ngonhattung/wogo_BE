package com.nhattung.wogo.service.worker.worktest;

import com.nhattung.wogo.constants.WogoConstants;
import com.nhattung.wogo.dto.request.WorkerVerificationTestRequestDTO;
import com.nhattung.wogo.dto.response.PageResponse;
import com.nhattung.wogo.dto.response.WorkerVerificationTestResponseDTO;
import com.nhattung.wogo.entity.WorkerVerificationTest;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.enums.TestStatus;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.WorkerVerificationTestRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                .build();
    }

    @Override
    public void updateWorkerVerificationTest(Long testId, WorkerVerificationTestRequestDTO request) {
        WorkerVerificationTest existingTest = workerVerificationTestRepository.findById(testId)
                .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));

        existingTest.setPassed(request.isPassed());
        existingTest.setCorrectAnswers(request.getCorrectAnswers());
        existingTest.setTestStatus(request.getTestStatus());
        existingTest.setCompletedAt(LocalDateTime.now());

        workerVerificationTestRepository.save(existingTest);
    }

    @Override
    public WorkerVerificationTest getWorkerVerificationTestById(Long testId) {
        return workerVerificationTestRepository.findById(testId)
                .orElseThrow(() -> new AppException(ErrorCode.TEST_NOT_FOUND));
    }

    @Override
    public PageResponse<WorkerVerificationTestResponseDTO> getAllWorkerVerificationTests(int page, int size) {
        if(page < 0 || size <= 0) {
            throw new AppException(ErrorCode.INVALID_PAGE_SIZE);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<WorkerVerificationTest> tests = workerVerificationTestRepository.findAll(pageable);
        List<WorkerVerificationTestResponseDTO> testDTOs = tests.stream()
                .map(this::convertToResponseDTO)
                .toList();
        return PageResponse.<WorkerVerificationTestResponseDTO>builder()
                .currentPage(page)
                .pageSize(size)
                .totalElements(tests.getTotalElements())
                .totalPages(tests.getTotalPages())
                .data(testDTOs)
                .build();
    }

    private WorkerVerificationTestResponseDTO convertToResponseDTO(WorkerVerificationTest test) {
        return modelMapper.map(test, WorkerVerificationTestResponseDTO.class);
    }


    private String generateTestCode() {
        // Logic to generate a unique test code
        return "Worker-TEST" + LocalDate.now().getYear() + "-" + System.currentTimeMillis();
    }
}
