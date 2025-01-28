package com.kakz.tradeIn.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kakz.tradeIn.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a user in the system.
 * This entity is mapped to a database table where user details are stored.
 *
 * The user has the following attributes:
 * - id: the unique identifier for the user.
 * - fullName: the full name of the user.
 * - email: the email address of the user.
 * - password: the user's password, not visible when fetching user information (write-only).
 * - twoFactorAuth: an embedded object for two-factor authentication details.
 * - role: the role of the user, defaulting to ROLE_CUSTOMER.
 */
@Entity
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String fullName;

    private String email;
    private String mobile;

    private boolean isVerified = false;

//    Whenever we Fetch user information password field will not appear it.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;
}
