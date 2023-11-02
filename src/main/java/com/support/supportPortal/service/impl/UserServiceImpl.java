package com.support.supportPortal.service.impl;

import com.support.supportPortal.constant.EmailConstant;
import com.support.supportPortal.domain.User;
import com.support.supportPortal.domain.UserPrincipal;
import com.support.supportPortal.enumeration.Role;
import com.support.supportPortal.exception.domain.EmailExistException;
import com.support.supportPortal.exception.domain.UsernameExistException;
import com.support.supportPortal.repository.UserRepository;
import com.support.supportPortal.service.EmailService;
import com.support.supportPortal.service.LoginAttempService;
import com.support.supportPortal.service.UserService;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;

import static com.support.supportPortal.constant.UserConstant.*;

import java.util.Date;
import java.util.List;

@Service
@Transactional
@Qualifier("userDetailService")
public class UserServiceImpl implements UserService, UserDetailsService {



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private LoginAttempService loginAttempService;
    @Autowired
    EmailService emailService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findUserByUsername(username);
        if(user==null)
            throw new UsernameNotFoundException("User Not found by username:"+username);
        else{
            validateLoginAttempt(user);
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal=new UserPrincipal(user);
            return userPrincipal;
        }

    }

    private void validateLoginAttempt(User user) {
        if(user.isNotLocked()){
            if(loginAttempService.hasExceededMaxAttempts(user.getUsername())){
                user.setNotLocked(false);
            }else{
                user.setNotLocked(true);
            }

        }else{
            System.out.println("evict");
            loginAttempService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) throws EmailExistException, UsernameExistException, MessagingException {
       validateNewUsernameAndEmail(StringUtils.EMPTY,username,email);
       User user=new User();
       user.setUserId(generateUserId());
       String password=generatePassword();
       System.out.println("PASSWORD:"+password);
       String encodedPassword=encodePassword(password);
       user.setFirstName(firstName);
       user.setLastName(lastName);
       user.setUsername(username);
       user.setEmail(email);
       user.setJoinDate(new Date());
       user.setPassword(encodedPassword);
       user.setActive(true);
       user.setNotLocked(true);
       user.setRoles(Role.ROLE_USER.name());
       user.setAuthorities(Role.ROLE_USER.getAuthorities());
       user.setProfileImageUrl(getTemperaryProfileImageURL());
       emailService.sendPassWordToEmail(password,email,firstName);
       userRepository.save(user);
       return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String getTemperaryProfileImageURL() {
        String baseURL= ServletUriComponentsBuilder.fromCurrentContextPath().path(USER_IMAGE_PROFILE_TEMP).toUriString();
        return baseURL;
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);

    }

    private User validateNewUsernameAndEmail(String currentUsername,String newUsername,String newEmail) throws UsernameExistException, EmailExistException {
        User userByUsername=findUserByUsername(newUsername);
        User userByEmail=findUserByEmail(newEmail);
        if(StringUtils.isNotEmpty(currentUsername)){
            //Handles the UPDATE USER
            User currentUser=findUserByUsername(currentUsername);
            if( currentUser==null)
                throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + " " +currentUsername);

            if(userByUsername!=null && ! currentUser.getId().equals(userByUsername.getUserId()))
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            if(userByEmail!=null && !currentUser.getId().equals(userByEmail.getId()))
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            return currentUser;
        }
        else{
            //Handles the NEW USER
            if(userByUsername!=null)
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            if(userByEmail!=null )
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            return userByUsername;
        }
    }

    @Override
    public List<User> getUser() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
