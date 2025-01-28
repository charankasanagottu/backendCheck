package com.kakz.tradeIn.model;

import com.kakz.tradeIn.domain.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ForgotPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private User user;

    private String otp;
    private VerificationType verificationType;
    private String sendTo;

}
