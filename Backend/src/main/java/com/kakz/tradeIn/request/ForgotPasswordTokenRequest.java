package com.kakz.tradeIn.request;

import com.kakz.tradeIn.domain.VerificationType;
import lombok.Data;


@Data
public class ForgotPasswordTokenRequest {
    private String sendTo;
    private VerificationType verificationType;
}

