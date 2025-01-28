package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.ForgotPasswordToken;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.domain.VerificationType;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String otp,
                                    VerificationType verificationType,String sendTo);

    ForgotPasswordToken findById(String id);
    ForgotPasswordToken findByUser(Long userId);
    void deleteToken(ForgotPasswordToken token);
    boolean verifyToken(ForgotPasswordToken token,String otp);
}
