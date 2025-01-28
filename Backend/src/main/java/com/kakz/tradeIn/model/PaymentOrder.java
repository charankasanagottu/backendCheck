package com.kakz.tradeIn.model;

import com.kakz.tradeIn.domain.PaymentMethod;
import com.kakz.tradeIn.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class PaymentOrder {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;

    private Long amount;

    private PaymentOrderStatus status;

    private PaymentMethod paymentMethod;

    @ManyToOne
    private User user;

}
