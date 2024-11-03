package com.application.microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.microservice.service.JwtService;
import com.application.microservice.service.OtpService;
import com.application.microservice.service.UserService;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import com.application.microservice.dto.*;
import com.application.microservice.service.*;
import com.application.microservice.repo.*;
import com.application.microservice.entity.*;
import java.util.Optional;


@RestController
@RequestMapping("/auth/")
public class HomeAuthController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private OtpService otpService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager; 

	//test mappings
	
	
	@GetMapping("hello")
	public String demo()
	{
		return "hello";
	}
	
	@GetMapping("test")
	public String test()
	{
		return "test purpose - how are you guys";
	}
	
	
	 //Required Mappings
	 
	 
	@PostMapping("register")
	public ResponseEntity<String> login(@RequestBody Ulogin ulogin)
	{
		return userService.saveUser(ulogin);
	}
	
	@PostMapping("login")
	public ResponseEntity<String> login(@RequestBody UsersLogin user)
	{	
		try
		{
			Authentication authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getEmailOrUsername(),user.getPassword()));
			
				return new ResponseEntity<>(jwtService.generateToken(user.getEmailOrUsername()),HttpStatus.OK);
		}
		catch(Exception e)
		{
			return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
		}
	}
	
	@PostMapping("forgotPassword")
	public ResponseEntity<String> forgetPassword(@RequestBody PasswordResetRequest passwordResetRequest)
	{
		String email = passwordResetRequest.getEmail();
		String otp = otpService.generateOtp();
		Optional<PasswordResetToken> resetToken = passwordResetTokenRepository.findByEmail(email);
		if(resetToken.isPresent())
		{
			passwordResetTokenRepository.delete(resetToken.get());
		}
		 if (!userService.existsByEmail(email)) {
		        return new ResponseEntity<>("Email not found", HttpStatus.NOT_FOUND);
		    }
		
		
		otpService.saveOtp(email,otp);
		emailService.sendOtpEmail(email,otp);
		
		return new ResponseEntity<>("OTP sent to email", HttpStatus.OK);
		
	}
	
	@GetMapping("validate")
    public ResponseEntity<String> testToken(@RequestParam String token) {
        // Validate the token (you'll implement this function)
        if (jwtService.validateToken(token)) {
            return ResponseEntity.ok("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
	
	//testing
	
	@PostMapping("otpVerification")
	public ResponseEntity<String> otpverified(@RequestBody VerificationRequest verificationRequest)
	{
		return userService.otpVerified(verificationRequest);
	}
	
	@PostMapping("resendOtp")
	public ResponseEntity<String> resendOtp(@RequestBody PasswordResetRequest passwordResetRequest) {
	    
		return userService.resendOtp(passwordResetRequest);
	}
	
	@PostMapping("resetPassword")
	public ResponseEntity<String> resetPasswordRequest(@RequestBody ResetPasswordRequest request) {
	    
		return userService.resetPassword(request);
	}

//	@PostMapping("resetPassword")
//	public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) 
//	{
//		 String email = request.getEmail();
//		 String otp = request.getOtp();
//		 String newPassword = request.getNewPassword();
//		 
//		 Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository.findByEmailAndOtp(email, otp);
//		 
//		 if (tokenOpt.isEmpty() || tokenOpt.get().isExpired()) {
//		        return new ResponseEntity<>("Invalid or expired OTP", HttpStatus.BAD_REQUEST);
//		    }
//		 
//		 Ulogin user = userService.findUserByEmail(email);
//		 userService.saveUser(user);
//		 user.setPassword(newPassword);
//		 passwordResetTokenRepository.delete(tokenOpt.get());
//		 return new ResponseEntity<>("Password updated successfully", HttpStatus.OK);
//	}
	
}
