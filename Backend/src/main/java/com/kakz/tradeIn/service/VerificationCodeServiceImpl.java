package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.VerificationCode;
import com.kakz.tradeIn.domain.VerificationType;
import com.kakz.tradeIn.repository.VerificationCodeRepository;
import com.kakz.tradeIn.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for managing verification codes.
 */
@Service
public class VerificationCodeServiceImpl implements VerificationCodeService{

    /**
     * Repository for managing persisting, retrieving, and deleting {@link VerificationCode} entities.
     * Injected into the service implementation to perform database operations.
     */
    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    /**
     * Sends a verification code to a user via the specified verification type.
     * Generates a one-time password (OTP) and creates a new {@link VerificationCode} record.
     *
     * @param user the user to whom the verification code is to be sent
     * @param verificationType the type of verification, such as MOBILE or EMAIL
     * @return the created {@link VerificationCode} with the generated OTP and specified verification type
     */
    @Override
    public VerificationCode sendVerificationCode(User user,
                                                 VerificationType verificationType ) {

        VerificationCode verificationCode1 = new VerificationCode();
        verificationCode1.setOtp(OtpUtils.generateOTP());
        verificationCode1.setVerificationType(verificationType);
        verificationCode1.setUser(user);
        verificationCode1.setEmail(user.getEmail());
        return verificationCodeRepository.save(verificationCode1);
    }

    /**
     * Retrieves a verification code by its ID.
     *
     * @param id the ID of the verification code to retrieve
     * @return the verification code associated with the given ID
     * @throws Exception if the verification code is not found
     */
    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
        Optional<VerificationCode> verificationCode = verificationCodeRepository.findById(id);
        if(verificationCode.isPresent()) {
            return verificationCode.get();
        }
        else{
            throw new Exception("Verification code not found");
        }
    }

    /**
     * Retrieves the verification code associated with a specific user by their user ID.
     *
     * @param userId the ID of the user whose verification code is to be retrieved
     * @return the verification code associated with the specified user
     */
    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {

        return verificationCodeRepository.findByUserId(userId);
    }

    /**
     * Deletes the provided verification code from the repository.
     *
     * @param verificationCode the verification code to delete
     */
    @Override
    public void deleteVerificationCode(VerificationCode verificationCode) {
        verificationCodeRepository.delete(verificationCode);
    }

    @Override
    public boolean verifyOtp(String otp, VerificationCode verificationCode) {
        return otp.equals(verificationCode.getOtp());
    }


}
