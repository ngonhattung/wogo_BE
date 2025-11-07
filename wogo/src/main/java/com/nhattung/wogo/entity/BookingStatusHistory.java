package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.ActorType;
import com.nhattung.wogo.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class BookingStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BookingStatus oldStatus;

    @Enumerated(EnumType.STRING)
    private BookingStatus newStatus;

    private Long changedByUserId;

    @Enumerated(EnumType.STRING)
    private ActorType changedBy;

    private String changeReason;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp changedAt;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
}
