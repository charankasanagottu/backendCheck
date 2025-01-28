package com.kakz.tradeIn.controller;


import com.kakz.tradeIn.request.ForgotPasswordTokenRequest;
import com.kakz.tradeIn.model.ForgotPasswordToken;
import com.kakz.tradeIn.model.User;
import com.kakz.tradeIn.model.VerificationCode;
import com.kakz.tradeIn.domain.VerificationType;
import com.kakz.tradeIn.request.ResetPasswordRequest;
import com.kakz.tradeIn.response.ApiResponse;
import com.kakz.tradeIn.response.AuthResponse;
import com.kakz.tradeIn.service.EmailService;
import com.kakz.tradeIn.service.ForgotPasswordService;
import com.kakz.tradeIn.service.UserService;
import com.kakz.tradeIn.service.VerificationCodeService;
import com.kakz.tradeIn.utils.OtpUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * UserController manages user-related operations including profile retrieval,
 * two-factor authentication, and password reset functionalities.
 */
@RestController
@RequestMapping("")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    /**
     * Retrieves the user profile based on the provided JWT token.
     *
     * @param jwt The JWT token from the Authorization header.
     * @return A ResponseEntity containing the User profile and an HTTP status code of OK.
     * @throws Exception If an error occurs during the retrieval of the user profile.
     */
    @GetMapping("/api/users/profile")
    @Operation(summary = "User Profile", description = "Retrieves the user profile based on the provided JWT token.")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user =  userService.findUserProfileByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Sends a verification OTP to the user based on the specified verification type.
     *
     * @param jwt the JSON Web Token (JWT) for user authentication
     * @param verificationType the type of verification (e.g., EMAIL, MOBILE)
     * @return a ResponseEntity containing a success message and HTTP status
     * @throws Exception if any error occurs during the process
     */
    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    @Operation(description= "Sends a verification OTP to the user based on the specified verification type" )
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt,
                                                      @PathVariable("verificationType") VerificationType verificationType) throws Exception {
        User user =  userService.findUserProfileByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if(verificationCode== null) {
            verificationCode = verificationCodeService.sendVerificationCode(user,verificationType);
        }
        if(verificationType.equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
        }
        return new ResponseEntity<>("Verification Otp Successfully Sent!", HttpStatus.OK);
    }

    /**
     * Enables two-factor authentication for the user after verifying the provided OTP.
     *
     * @param jwt The JSON Web Token (JWT) from the Authorization header.
     * @param otp The one-time password (OTP) for verification.
     * @return A ResponseEntity containing the updated User and an HTTP status code of OK if successful.
     * @throws Exception If an error occurs during the verification of the OTP or enabling two-factor authentication.
     */
    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    @Operation(summary = "Enable two-factor authentication")
    public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt,
                                                              @PathVariable("otp") String otp) throws Exception {
        User user =  userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)?
                verificationCode.getEmail(): verificationCode.getMobile();

        boolean isVerified = verificationCodeService.verifyOtp(otp,verificationCode);
        if(isVerified) {
            User updatedUser = userService.enableTwoFactorAuthentication(
                    verificationCode.getVerificationType(),
                    sendTo,
                    user);
            verificationCodeService.deleteVerificationCode(verificationCode);
            return new ResponseEntity<>(updatedUser,HttpStatus.OK);
        }
        throw new Exception("Wrong Otp");
    }

    /**
     * Sends a forgot password OTP to the user's email for password reset purposes.
     *
     * @param request the forgot password token request containing the email to send to and the verification type
     * @return a ResponseEntity containing an AuthResponse with session information and a success message
     * @throws Exception if any error occurs during the process
     */
    @Operation(summary = "Send forgot password OTP to the user's email for password reset'")
    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest request)
            throws Exception {

        User user = userService.findUserByEmail(request.getSendTo());
        String otp = OtpUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
//        String id = uuid.toString();
        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());
        if(token == null){
            token = forgotPasswordService.
                    createToken(user,otp,
                            request.getVerificationType(),
                            request.getSendTo());

        }
        if(request.getVerificationType().equals(VerificationType.EMAIL)){
            emailService.sendVerificationOtpEmail(
                    user.getEmail(),
                    token.getOtp());
        }
        AuthResponse authResponse = new AuthResponse();
        authResponse.setSession(token.getId().toString());
        authResponse.setMessage("Password reset Otp Sent successfully");

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    /**
     * Resets the user's password by verifying the provided OTP (one-time password).
     *
     * @param jwt The JSON Web Token for user authentication from the Authorization header.
     * @param req The ResetPasswordRequest object containing the OTP and the new password.
     * @param id The ID of the ForgotPasswordToken entity.
     * @return A ResponseEntity containing an ApiResponse message and an HTTP status code.
     * @throws Exception If the OTP is incorrect or any other error occurs during the password reset process.
     */
    @Operation(summary = "Resets the user's password'")
    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(
                                             @RequestBody ResetPasswordRequest req,
                                              @RequestParam String id) throws Exception {

        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

        boolean isVerified = forgotPasswordService.verifyToken(forgotPasswordToken,req.getOtp());

        if(isVerified){
            userService.updatePassword(forgotPasswordToken.getUser(), req.getPassword());
            ApiResponse apiResponse= new ApiResponse();
            apiResponse.setMessage("Password Updated successfully");
            return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
        }
        throw new Exception("Wrong Otp Token");
    }

    @GetMapping("/api/users/{userId}")
    public ResponseEntity<User> findUserById(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByUserId(userId);
        user.setPassword(null);

        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @GetMapping("/api/users/email/{email}")
    public ResponseEntity<User> findUserByEmail(
            @PathVariable String email,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByEmail(email);

        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }

    @PatchMapping("/api/users/verification/verify-otp/{otp}")
    public ResponseEntity<User> verifyOTP(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String otp
    ) throws Exception {


        User user = userService.findUserProfileByJwt(jwt);


        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());


        boolean isVerified = verificationCodeService.verifyOtp(otp, verificationCode);

        if (isVerified) {
            verificationCodeService.deleteVerificationCode(verificationCode);
            User verifiedUser = userService.verifyUser(user);
            return ResponseEntity.ok(verifiedUser);
        }
        throw new Exception("wrong otp");

    }
}
