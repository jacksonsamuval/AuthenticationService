package com.application.microservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.application.microservice.dto.PasswordResetRequest;
import com.application.microservice.dto.ResetPasswordRequest;
import com.application.microservice.dto.VerificationRequest;
import com.application.microservice.entity.PasswordResetToken;
import com.application.microservice.entity.Ulogin;
import com.application.microservice.repo.PasswordResetTokenRepository;
import com.application.microservice.repo.UserRepo;
import jakarta.transaction.Transactional;

@Service
public class UserService {
	
	@Autowired
	private UserRepo repo;
	
	@Autowired
	private OtpService otpService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	public ResponseEntity<String> saveUser(Ulogin user)
	{
		if(user==null || user.getEmail()==null)
		{
			return new ResponseEntity<>("Invalid User Details",HttpStatus.BAD_REQUEST);
		}
		
		 if (repo.findByEmail(user.getEmail()).isPresent()) 
		 {
			 return new ResponseEntity<>("Email Exists",HttpStatus.BAD_REQUEST);
		 }
		 else
		 {
			 repo.save(user);
			 return new ResponseEntity<>("Success",HttpStatus.OK);
		 }
		
		
	}
	
	@Transactional
	public Ulogin findUserByEmail(String email)
	{
		return repo.findUserByEmail(email);
	}

	public ResponseEntity<String> otpVerified(VerificationRequest verificationRequest) {
		
		String email = verificationRequest.getEmail();
		String otp = verificationRequest.getOtp();
		
		Optional<PasswordResetToken> tokenOtp = passwordResetTokenRepository.findByEmailAndOtp(email,otp);
		if (tokenOtp.isPresent()) {
		    if (tokenOtp.get().isExpired()) {
		        return new ResponseEntity<>("Otp Expired", HttpStatus.BAD_REQUEST);
		    }
		    if (!tokenOtp.get().getOtp().equals(otp)) {
		        return new ResponseEntity<>("Otp Not Valid", HttpStatus.BAD_REQUEST);
		    }
		    passwordResetTokenRepository.delete(tokenOtp.get());
		    return new ResponseEntity<>("Otp Successfully verified", HttpStatus.OK);
		} else {
		    return new ResponseEntity<>("Otp Not Valid", HttpStatus.BAD_REQUEST);
		}

	}

	public ResponseEntity<String> resendOtp(PasswordResetRequest passwordResetRequest) {
		String email = passwordResetRequest.getEmail();
	    Optional<PasswordResetToken> resetToken = passwordResetTokenRepository.findByEmail(email);

	    // Check if the token exists and is still valid (within 10 minutes)
	    
	    if (resetToken.isPresent() && !otpService.isExpired(resetToken.get())) {
	        return new ResponseEntity<>("OTP was already sent recently. Please check your email.", HttpStatus.TOO_MANY_REQUESTS);
	    }

	    // Generate new OTP and save
	    String otp = otpService.generateOtp();
	    resetToken.ifPresent(passwordResetTokenRepository::delete);
	    otpService.saveOtp(email, otp);
	    emailService.sendOtpEmail(email, otp);

	    return new ResponseEntity<>("New OTP sent to email", HttpStatus.OK);
	}

	public ResponseEntity<String> resetPassword(ResetPasswordRequest request) {
		String email = request.getEmail();
	    String newPassword = request.getNewPassword();

	    Ulogin user = repo.findUserByEmail(email);
	    
	    if (user == null) {
	        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
	    }
	    user.setPassword(newPassword);
	    
	    repo.save(user);

	    return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
	}

	
}
