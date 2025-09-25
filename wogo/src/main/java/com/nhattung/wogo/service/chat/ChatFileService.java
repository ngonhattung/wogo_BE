package com.nhattung.wogo.service.chat;

import com.nhattung.wogo.dto.response.UploadS3Response;
import com.nhattung.wogo.entity.ChatFile;
import com.nhattung.wogo.entity.ChatMessage;
import com.nhattung.wogo.entity.JobFile;
import com.nhattung.wogo.enums.ErrorCode;
import com.nhattung.wogo.exception.AppException;
import com.nhattung.wogo.repository.ChatFileRepository;
import com.nhattung.wogo.utils.UploadToS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatFileService implements IChatFileService {

    private final ChatFileRepository chatFileRepository;
    private final UploadToS3 uploadToS3;

    @Override
    public void saveChatFile(ChatMessage chatMessage, List<MultipartFile> files) {
        if(files == null || files.isEmpty()) {
            return; // No files to process
        }
        for (MultipartFile file : files) {
            try {
                // Upload to S3
                UploadS3Response s3Response = uploadToS3.uploadFileToS3(file);

                // Save to database
                chatFileRepository.save(
                        ChatFile.builder()
                                .chatMessage(chatMessage)
                                .fileName(s3Response.getFileName())
                                .fileType(s3Response.getFileType())
                                .fileUrl(s3Response.getFileUrl())
                                .build()
                );
            } catch (Exception e) {
                // Log the error and throw a custom exception
                e.printStackTrace();
                throw new AppException(ErrorCode.UPLOAD_IMAGE_ERROR);
            }
        }
    }

    @Override
    public List<ChatFile> getChatFilesByMessageId(Long messageId) {
        return chatFileRepository.findByChatMessageId(messageId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_FILE_NOT_FOUND));
    }
}
