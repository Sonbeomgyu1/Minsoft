package com.mysite.minsoft.login.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysite.minsoft.login.model.SiteUser;
import com.mysite.minsoft.login.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public SiteUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null; // 미인증 사용자 또는 로그인하지 않은 경우 null 반환
		}

		String username = authentication.getName();
		Optional<SiteUser> userOptional = userRepository.findByUsername(username);

		return userOptional.orElse(null);
	}

	// 회원가입 정보 저장
	public SiteUser create(String username, String password, String name) {
		SiteUser user = new SiteUser();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setName(name);
		this.userRepository.save(user);

		return user;
	}
	
	 public List<SiteUser> getAllUsers() {
	        return userRepository.findAll();
	    }

	    public void deleteUser(String username) {
	    	userRepository.deleteById(username);
	    }

	    public Optional<SiteUser> getByUsername(String username) {
	        return userRepository.findByUsername(username);
	    }

	    public Optional<SiteUser> getByName(String name) {
	        return userRepository.findByName(name);
	    }
}