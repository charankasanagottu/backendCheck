package com.kakz.tradeIn.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String otp;
    private String password;

}
