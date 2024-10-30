package com.application.microservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.application.microservice.entity.Ulogin;
import com.application.microservice.repo.UserRepo;
import jakarta.transaction.Transactional;

@Service
public class UserService {
	@Autowired
	private UserRepo repo;
	
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
}
