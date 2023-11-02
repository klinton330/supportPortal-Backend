package com.support.supportPortal.controller;

import com.support.supportPortal.constant.SecurityConstant;
import com.support.supportPortal.domain.User;
import com.support.supportPortal.domain.UserPrincipal;
import com.support.supportPortal.exception.domain.EmailExistException;
import com.support.supportPortal.exception.domain.ExceptionHandlers;
import com.support.supportPortal.exception.domain.UsernameExistException;
import com.support.supportPortal.repository.UserRepository;
import com.support.supportPortal.service.UserService;
import com.support.supportPortal.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequestMapping(path ={"/","/user"})
public class UserController extends ExceptionHandlers {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @GetMapping("/home")
    public String showUser()  {
        return "Application works";

    }


    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody User user) throws EmailExistException, UsernameExistException, MessagingException {
        User registeduser= userService.register(user.getFirstName(),user.getLastName(),user.getUsername(),user.getEmail());
        return new ResponseEntity<>(registeduser, HttpStatus.OK);

    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user) {
        authenticate(user.getUsername(),user.getPassword());
        User getUser=userRepository.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal=new UserPrincipal(getUser);
        HttpHeaders httpHeaders=getJwtHeader(userPrincipal);
        return new ResponseEntity<>(getUser,httpHeaders, HttpStatus.OK);

    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        HttpHeaders headers=new HttpHeaders();
        headers.add(SecurityConstant.JWT_TOKEN_HEADER,jwtTokenProvider.generateJwtToken(userPrincipal));
        return headers;
    }

    private void authenticate(String username, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,password));
    }
}
