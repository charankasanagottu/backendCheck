package com.kakz.tradeIn.service;

import com.kakz.tradeIn.config.JwtProvider;
import com.kakz.tradeIn.model.TwoFactorAuth;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.domain.VerificationType;
import com.kakz.tradeIn.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the {@link UserService} interface that provides
 * user-related services such as finding user profiles, enabling
 * two-factor authentication, and updating passwords.
 */
@Service
public class UserServiceImpl implements UserService{
    /**
     * Repository interface for {@link User} entity that provides CRUD operations
     * and custom query methods related to users.
     */
    @Autowired
    private UserRepository userRepository;


    /**
     * Finds a user profile based on the provided JSON Web Token (JWT).
     *
     * @param jwt the JSON Web Token used to extract the user's email.
     * @return the user profile corresponding to the email extracted from the JWT.
     * @throws Exception if the user could not be found by the extracted email.
     */
    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromJwtToken(jwt);
        User user = findUserByEmail(email);
        return user;
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to find
     * @return the User object associated with the given email address
     * @throws Exception if no user is found with the given email address
     */
    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("Could not find User by email " + email);
        }
        return user;
    }

    /**
     * Finds a user by their unique user ID.
     *
     * @param id the ID of the user to be found.
     * @return the User object corresponding to the given ID.
     * @throws Exception if a user with the provided ID is not found.
     */
    @Override
    public User findUserByUserId(Long id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new Exception("Could not find User by id " + id);
        }
        return user.get();
    }

    /**
     * Enables two-factor authentication for the specified user.
     *
     * @param verificationType the type of verification to be used (e.g., MOBILE, EMAIL)
     * @param sendTo the recipient details where the verification code should be sent
     * @param user the user for whom two-factor authentication should be enabled
     * @return the updated user with two-factor authentication enabled
     */
    @Override
    public User enableTwoFactorAuthentication(VerificationType verificationType,
                                              String sendTo,User user) {

        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);

        user.setTwoFactorAuth(twoFactorAuth);

        userRepository.save(user);

        return user;
    }

    /**
     * Updates the password for the specified user.
     *
     * @param user the user whose password will be updated
     * @param newPassword the new password to be set for the user
     * @return the updated user with the new password
     */
    @Override
    public User updatePassword(User user,
                               String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public User verifyUser(User user) throws ExecutionControl.UserException {
        user.setVerified(true);
        return userRepository.save(user);
    }
}
