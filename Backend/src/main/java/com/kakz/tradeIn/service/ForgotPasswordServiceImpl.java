package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.ForgotPasswordToken;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.domain.VerificationType;
import com.kakz.tradeIn.repository.ForgotPasswordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for managing forgot password tokens.
 */
@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService{
    /**
     * Repository for performing CRUD operations on ForgotPasswordToken entities.
     */
    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    /**
     * Creates a new forgot password token for a given user.
     *
     * @param user the user for whom the token is created
     * @param id the unique identifier for the token
     * @param otp the one-time password for the token
     * @param verificationType the type of verification (e.g., MOBILE, EMAIL)
     * @param sendTo the recipient to whom the OTP is sent
     * @return the created ForgotPasswordToken entity
     */
    @Override
    public ForgotPasswordToken createToken(User user,
//                                           String id,
                                           String otp,
                                           VerificationType verificationType,
                                           String sendTo) {

        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setOtp(otp);
        token.setSendTo(sendTo);
        token.setVerificationType(verificationType);
        token.setUser(user);
//        token.setId(Long.parseLong(id));
        return forgotPasswordRepository.save(token);
    }

    /**
     * Retrieves a ForgotPasswordToken by its unique identifier.
     *
     * @param id the unique identifier of the ForgotPasswordToken
     * @return the corresponding ForgotPasswordToken if found, null otherwise
     */
    @Override
    public ForgotPasswordToken findById(String id) {
        Optional<ForgotPasswordToken> token = forgotPasswordRepository.findById(id);
        return token.orElse(null);
    }

    /**
     * Finds and returns a ForgotPasswordToken associated with a specific user.
     *
     * @param userId the unique identifier of the user whose forgot password token needs to be retrieved.
     * @return the ForgotPasswordToken associated with the given user ID, or null if no such token is found.
     */
    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return forgotPasswordRepository.findByUserId(userId);
    }

    /**
     * Deletes a given ForgotPasswordToken from the repository.
     *
     * @param token the ForgotPasswordToken to be deleted
     */
    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordRepository.delete(token);
    }

    @Override
    public boolean verifyToken(ForgotPasswordToken token, String otp) {
        return token.getOtp().equals(otp);
    }
}
