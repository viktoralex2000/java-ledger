package io.github.viktoralex2000.ledger.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

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

    @Column(name = "role", nullable = false, length = 16)
    private String role;
    //* role : VARCHAR(16)

    @Column(name = "status", nullable = false, length = 16)
    private String status;
    //* status : VARCHAR(16)

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;
    //* failed_login_attempts : INTEGER

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    //* created_at : TIMESTAMPTZ

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    //* updated_at : TIMESTAMPTZ

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