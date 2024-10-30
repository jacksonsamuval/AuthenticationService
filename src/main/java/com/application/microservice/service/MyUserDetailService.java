package com.application.microservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.application.microservice.entity.*;
import com.application.microservice.entity.Ulogin;
import com.application.microservice.repo.UserRepo;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {
	
	@Autowired
	private UserRepo repo;

//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		Ulogin user = repo.findByUsername(username);
//		if(user==null)
//		{
//			throw new UsernameNotFoundException("User 404");
//		}
//		return new UserPrincipal(user);
//	}
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Ulogin user = repo.findUserByEmail(email);
		if(user==null)
		{
			throw new UsernameNotFoundException("User 404", null);
		}
		return new UserPrincipal(user);
	}

}
