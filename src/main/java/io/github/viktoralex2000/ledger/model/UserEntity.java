package io.github.viktoralex2000.ledger.model;

import io.github.viktoralex2000.ledger.model.enums.Role;
import io.github.viktoralex2000.ledger.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // * id : BIGINT «PK»

    @Column(name = "email", nullable = false, unique = true)
    private String email;
    // * email : VARCHAR(255) «UK»

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    //* password_hash : VARCHAR(255)

    @Column(name = "first_name", length = 120)
    private String firstName;
    //first_name : VARCHAR(120)

    @Column(name = "last_name", length = 120)
    private String lastName;
    //last_name : VARCHAR(120)

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 16)
    private Role role = Role.USER;
    //* role : VARCHAR(16)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private UserStatus status = UserStatus.ACTIVE;
    //* status : VARCHAR(16)

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;
    //* failed_login_attempts : INTEGER

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    //* created_at : TIMESTAMPTZ

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    //* updated_at : TIMESTAMPTZ

    //Методы

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    @PrePersist
    private void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = Instant.now();
    }

}
/*
    CREATE TABLE IF NOT EXISTS users (
        id BIGSERIAL PRIMARY KEY,
        email VARCHAR(255) NOT NULL UNIQUE,
        password_hash VARCHAR(255) NOT NULL,
        first_name VARCHAR(120),
        last_name VARCHAR(120),
        role VARCHAR(16) NOT NULL,
        status VARCHAR(16) NOT NULL,
        failed_login_attempts INTEGER NOT NULL DEFAULT 0,
        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );
 */