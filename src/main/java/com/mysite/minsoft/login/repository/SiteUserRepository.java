package com.mysite.minsoft.login.repository;

import com.mysite.minsoft.login.model.SiteUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteUserRepository extends JpaRepository<SiteUser, String> {
	
	Optional<SiteUser> findByUsername(String username);
    Optional<SiteUser> findByName(String name);
}
