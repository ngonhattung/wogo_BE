package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.WorkStatus;
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
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private double ratingAverage;
    private int totalJobs;
    private int totalReviews;

    @Enumerated(EnumType.STRING)
    private WorkStatus status; // AVAILABLE, IN_PROGRESS, BUSY

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkerService> workerServices;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

//    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ChatRoom> chatRooms;

    @OneToOne(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkerWalletRevenue workerWalletRevenue;

    @OneToOne(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkerWalletExpense workerWalletExpense;

    @OneToOne(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private Deposit deposit;

    @OneToOne(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private Withdrawal withdrawal;

    @OneToMany(mappedBy = "worker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkerQuote> workerQuotes;
}
