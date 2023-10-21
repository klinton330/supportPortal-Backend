package com.support.supportPortal.controller;

import com.support.supportPortal.exception.domain.EmailExistException;
import com.support.supportPortal.exception.domain.ExceptionHandlers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path ={"/","/user"})
public class UserController extends ExceptionHandlers {

    @GetMapping("/home")
    public String showUser()  {
        return "Application works";

    }
}
