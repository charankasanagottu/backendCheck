package com.kakz.tradeIn.response;

import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private Boolean status;
    private String message;
    private Boolean isTwoFactorAuthEnabled = false;

    private String session;

}
