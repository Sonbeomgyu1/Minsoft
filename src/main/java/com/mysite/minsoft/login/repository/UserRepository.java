package com.mysite.minsoft.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mysite.minsoft.login.model.SiteUser;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SiteUser, String> {
    Optional<SiteUser> findByUsername(String username);
    Optional<SiteUser> findByName(String name);
}
