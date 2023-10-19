package com.support.supportPortal.repository;

import com.support.supportPortal.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {

    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
