package com.application.microservice.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.application.microservice.entity.PasswordResetToken;
import com.application.microservice.entity.Ulogin;

@Repository
public interface UserRepo extends JpaRepository<Ulogin, Integer> {


	Ulogin findUserByEmail(String email);

	Ulogin findByUsername(String username);
	
	Optional<PasswordResetToken> findUserUsingByEmail(String email);
	
	Optional<Ulogin> findByEmail(String email);

	
	

}
