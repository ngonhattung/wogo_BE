package com.nhattung.wogo.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomCode; // Mã định danh duy nhất cho phòng chat
    private boolean isVisible; // Có hiển thị với user không
    private LocalDateTime lastMessageAt; // thời gian gửi tin nhắn cuối

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user; // Khách
//
//    @ManyToOne
//    @JoinColumn(name = "worker_id", nullable = false)
//    private Worker worker; // Thợ

    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job; // Liên kết job

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> chatMessages; // Danh sách tin nhắn
}
