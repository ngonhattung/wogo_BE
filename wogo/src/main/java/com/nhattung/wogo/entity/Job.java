package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.Canceller;
import com.nhattung.wogo.enums.JobRequestStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.java.Log;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobRequestCode;
    private LocalDateTime bookingDate;
    private double latitude;
    private double longitude;
    private String description;
    private String bookingAddress;
    private BigDecimal estimatedPriceLower;
    private BigDecimal estimatedPriceHigher;
    private int estimatedDurationMinutes;
    private Long acceptedBy;
    private String cancelReason;

    @Enumerated(EnumType.STRING)
    private Canceller cancelledBy;

    @Enumerated(EnumType.STRING)
    private JobRequestStatus status;


    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkerQuote> workerQuotes;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobFile> jobFiles;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceWG service;

//    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ChatRoom> chatRooms;


    @CreationTimestamp
    private LocalDateTime createdAt;
}
