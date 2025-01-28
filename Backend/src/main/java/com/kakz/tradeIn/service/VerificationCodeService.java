package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.VerificationCode;
import com.kakz.tradeIn.domain.VerificationType;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long id) throws Exception;
    VerificationCode getVerificationCodeByUser(Long userId);
    void deleteVerificationCode(VerificationCode verificationCode);

    boolean verifyOtp(String otp,VerificationCode verificationCode);
}
