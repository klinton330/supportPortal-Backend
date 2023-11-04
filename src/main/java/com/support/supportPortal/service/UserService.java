package com.support.supportPortal.service;


import com.support.supportPortal.domain.User;
import com.support.supportPortal.exception.domain.EmailExistException;
import com.support.supportPortal.exception.domain.EmailNotFoundException;
import com.support.supportPortal.exception.domain.UsernameExistException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.io.IOException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException, MessagingException;
    List<User> getUser();
    User findUserByUsername(String username);
    User findUserByEmail(String email);

    User addNewUser(String firstName,String lastName, String userName, String email, String role, boolean isNonLocked, boolean isActive , MultipartFile multipart) throws EmailExistException, UsernameExistException, IOException;
    User updateUser(String currentUserName,String newFirstName,String newLastName, String newUserName, String newEmail, String role, boolean isNonLocked, boolean isActive , MultipartFile multipart) throws EmailExistException, UsernameExistException, IOException;

    void deleteUser(long id);
    void resetPassword(String email) throws EmailNotFoundException;
    User updateUserProfileImage(String username,MultipartFile multipart) throws EmailExistException, UsernameExistException, IOException;
}
