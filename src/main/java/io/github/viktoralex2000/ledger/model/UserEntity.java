package io.github.viktoralex2000.ledger.model;

import io.github.viktoralex2000.ledger.model.enums.Role;
import io.github.viktoralex2000.ledger.model.enums.UserStatus;
import jakarta.persistence.*;
<<<<<<< HEAD
import lombok.*;

import org.hibernate.annotations.*;
=======
import lombok.Getter;
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

import java.time.Instant;
<<<<<<< HEAD
import java.time.OffsetDateTime;
=======
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

<<<<<<< HEAD
=======
@Getter
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git
@Entity
@Table(name = "users")
public class UserEntity {

    public enum Role { USER, ADMIN, SYSTEM }
    public enum Status { ACTIVE, LOCKED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", length = 120)
    private String firstName;

    @Column(name = "last_name", length = 120)
    private String lastName;

    @Enumerated(EnumType.STRING)
<<<<<<< HEAD
    @Column(nullable = false, length = 16)
    private Role role = Role.USER;
=======
    @Column(name = "role", nullable = false, length = 16)
    private Role role = Role.USER;
    //* role : VARCHAR(16)
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

    @Enumerated(EnumType.STRING)
<<<<<<< HEAD
    @Column(nullable = false, length = 16)
    private Status status = Status.ACTIVE;
=======
    @Column(name = "status", nullable = false, length = 16)
    private UserStatus status = UserStatus.ACTIVE;
    //* status : VARCHAR(16)
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
<<<<<<< HEAD
    private Instant createdAt = Instant.now();
=======
    private Instant createdAt;
    //* created_at : TIMESTAMPTZ
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

    @Column(name = "updated_at", nullable = false)
<<<<<<< HEAD
    private Instant updatedAt = Instant.now();
=======
    private Instant updatedAt;
    //* updated_at : TIMESTAMPTZ
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

<<<<<<< HEAD
    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
=======
    //Методы
>>>>>>> branch 'create-the-project-structure' of https://github.com/viktoralex2000/java-ledger.git

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