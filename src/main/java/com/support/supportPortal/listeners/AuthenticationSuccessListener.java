package com.support.supportPortal.listeners;

import com.support.supportPortal.domain.User;
import com.support.supportPortal.domain.UserPrincipal;
import com.support.supportPortal.service.LoginAttempService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;
//Fires everytime user successfully enter the credential
@Component
public class AuthenticationSuccessListener {
    @Autowired
    private LoginAttempService loginAttempService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event){
        Object principal=event.getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal){
            UserPrincipal user=(UserPrincipal)event.getAuthentication().getPrincipal();
            loginAttempService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
