package com.application.microservice.repo;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.microservice.entity.PasswordResetToken;
import com.application.microservice.entity.Ulogin;


public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByEmailAndOtp(String email, String otp);

	Optional<PasswordResetToken> findByEmail(String email);

}