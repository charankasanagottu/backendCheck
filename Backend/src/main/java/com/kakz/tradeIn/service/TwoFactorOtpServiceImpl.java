package com.kakz.tradeIn.service;

import com.kakz.tradeIn.model.TwoFactorOTP;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.repository.TwoFactorOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing two-factor authentication OTP (One-Time Password).
 * This service provides methods to create, find, verify, and delete OTPs for two-factor authentication.
 */
@Service
public class TwoFactorOtpServiceImpl implements TwoFactorOtpService{

    /**
     * Repository for managing TwoFactorOTP entities.
     * Used for performing CRUD operations on TwoFactorOTP objects.
     */
    @Autowired
    private TwoFactorOtpRepository twoFactorOtpRepository;


    /**
     * Creates a new two-factor authentication OTP (One-Time Password) for the specified user.
     *
     * @param user the user for whom the OTP is being created
     * @param otp the one-time password to be associated with the user
     * @param jwt the JWT token to be associated with the OTP
     * @return the created TwoFactorOTP entity
     */
    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setJwt(jwt);
        twoFactorOTP.setId(id);
        twoFactorOTP.setUser(user);

        return twoFactorOtpRepository.save(twoFactorOTP);
    }

    /**
     * Retrieve the TwoFactorOTP associated with a given user ID.
     *
     * @param userId the ID of the user whose TwoFactorOTP is to be retrieved
     * @return the TwoFactorOTP associated with the given user ID, or null if none found
     */
    @Override
    public TwoFactorOTP findByUser(Long userId) {
        return twoFactorOtpRepository.findByUserId(userId);
    }

    /**
     * Finds a TwoFactorOTP entity by its unique identifier.
     *
     * @param id the unique identifier of the TwoFactorOTP entity to be retrieved
     * @return the TwoFactorOTP entity if found, otherwise null
     */
    @Override
    public TwoFactorOTP findById(String id) {
        Optional<TwoFactorOTP> opt = twoFactorOtpRepository.findById(id);
        return opt.orElse(null);
    }

    /**
     * Verifies if the provided OTP matches the stored OTP in the given TwoFactorOTP object.
     *
     * @param twoFactorOTP The TwoFactorOTP object which contains the stored OTP for verification.
     * @param otp The One-Time Password (OTP) to be verified.
     * @return true if the provided OTP matches the stored OTP; false otherwise.
     */
    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp) {
        return twoFactorOTP.getOtp().equals(otp);
    }

    /**
     * Deletes a given TwoFactorOTP entity from the repository.
     *
     * @param twoFactorOTP the TwoFactorOTP entity to be deleted
     */
    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP) {
        twoFactorOtpRepository.delete(twoFactorOTP);
    }
}
