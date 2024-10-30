package com.application.microservice.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.application.microservice.repo.PasswordResetTokenRepository;
import com.application.microservice.entity.PasswordResetToken;
import java.time.Duration;
import java.time.Instant;
import java.util.Random;

@Service
public class OtpService {
	
	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;
	
	public String generateOtp()
	{
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}
	
	public void saveOtp(String email, String otp)
	{
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setEmail(email);
		resetToken.setOtp(otp);
		resetToken.setExpiryDate(Instant.now().plus(Duration.ofMinutes(10)));
		passwordResetTokenRepository.save(resetToken);
	}

}
