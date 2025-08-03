package com.nhattung.wogo.entity;

import com.nhattung.wogo.enums.PaymentMethodE;
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
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentMethodE paymentMethod;

    private String description;
    private boolean isActive;

    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToOne(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true)
    private BookingPayment bookingPayment;

    @OneToOne(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true)
    private TopupRequest topupRequest;

}
