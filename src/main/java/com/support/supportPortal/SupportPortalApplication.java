package com.support.supportPortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

import static com.support.supportPortal.constant.FileConstant.USER_FOLDER;

@SpringBootApplication
public class SupportPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupportPortalApplication.class, args);
		System.out.println(System.getProperty("user.home"));//C:\Users\HOME
		new File(USER_FOLDER).mkdir();
	}

}
