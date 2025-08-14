package com.nhattung.wogo.service.testanswer;

import com.nhattung.wogo.dto.request.TestAnswerRequestDTO;
import com.nhattung.wogo.entity.Question;
import com.nhattung.wogo.entity.QuestionOption;
import com.nhattung.wogo.entity.TestAnswer;
import com.nhattung.wogo.entity.WorkerVerificationTest;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.QuestionOptionRepository;
import com.nhattung.wogo.repository.QuestionRepository;
import com.nhattung.wogo.repository.TestAnswerRepository;
import com.nhattung.wogo.repository.WorkerVerificationTestRepository;
import com.nhattung.wogo.service.question.IQuestionService;
import com.nhattung.wogo.service.workerverificationtest.IWorkerVerificationTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestAnswerService implements ITestAnswerService {

    private final TestAnswerRepository testAnswerRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    @Override
    public void saveTestAnswer(TestAnswerRequestDTO request) {
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        QuestionOption questionOption = questionOptionRepository.findById(request.getQuestionOptionId())
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_OPTION_NOT_FOUND));

        TestAnswer testAnswer = createTestAnswer(request,request.getWorkerTest(), questionOption, question);
        testAnswerRepository.save(testAnswer);
    }

    private TestAnswer createTestAnswer(TestAnswerRequestDTO request,WorkerVerificationTest workerVerificationTest,
                                        QuestionOption questionOption, Question question) {
        return TestAnswer.builder()
                .workerVerificationTest(workerVerificationTest)
                .questionOption(questionOption)
                .question(question)
                .isCorrect(questionOption.isCorrect())
                .build();
    }
}
