package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.TwoFactorOTP;
import com.kakz.tradeIn.model.User;

public interface TwoFactorOtpService {
    TwoFactorOTP createTwoFactorOtp(User user, String otp,String jwt);
    TwoFactorOTP findByUser(Long userId);
    TwoFactorOTP findById(String id);
    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP,String otp);
    void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP);
}
