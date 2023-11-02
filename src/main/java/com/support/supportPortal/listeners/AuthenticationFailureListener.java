package com.support.supportPortal.listeners;

import com.support.supportPortal.service.LoginAttempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

//Fires everytime user fail to enter correct password
@Component
public class AuthenticationFailureListener {
    @Autowired
    private LoginAttempService loginAttempService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event){
        Object principal=event.getAuthentication().getPrincipal();//username
        if(principal instanceof String){
            String user=(String) event.getAuthentication().getPrincipal();
            loginAttempService.addUserToLoginAttemptCache(user);
        }
    }
}
