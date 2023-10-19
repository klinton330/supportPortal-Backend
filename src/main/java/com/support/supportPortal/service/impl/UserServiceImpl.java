package com.support.supportPortal.service.impl;

import com.support.supportPortal.domain.User;
import com.support.supportPortal.domain.UserPrincipal;
import com.support.supportPortal.repository.UserRepository;
import com.support.supportPortal.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Transactional
@Qualifier("userDetailService")
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepository.findUserByUsername(username);
        if(user==null)
            throw new UsernameNotFoundException("User Not found by username:"+username);
        else{
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal=new UserPrincipal(user);
            return userPrincipal;
        }

    }
}
