package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.MessageType;
import com.nhattung.wogo.enums.SenderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType messageType; // TEXT, IMAGE, FILE...

    private String content;

    private boolean isRead;
    private boolean isDeleted;
    private Long replyToMessageId;

    @Enumerated(EnumType.STRING)
    private SenderType senderType; // USER hoặc WORKER

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;

//    @ManyToOne
//    @JoinColumn(name = "worker_id")
//    private Worker worker; // nếu senderType = WORKER

    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatFile> chatFiles;
}
