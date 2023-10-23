package com.support.supportPortal.service;


import com.support.supportPortal.domain.User;
import com.support.supportPortal.exception.domain.EmailExistException;
import com.support.supportPortal.exception.domain.UsernameExistException;

import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException;
    List<User> getUser();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
