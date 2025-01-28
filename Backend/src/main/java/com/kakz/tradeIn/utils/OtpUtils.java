package com.kakz.tradeIn.utils;

import java.util.Random;

public class OtpUtils {
    /**
     * Generates a one-time password (OTP) of fixed length consisting of random digits.
     *
     * @return a String representing the generated OTP
     */
    public static String generateOTP(){
        int otpLength = 6;
        Random rand = new Random();
        StringBuilder otp = new StringBuilder(otpLength);
        for(int i=0; i<otpLength; i++){
            otp.append(rand.nextInt(10));
        }
        return otp.toString();
    }
}
