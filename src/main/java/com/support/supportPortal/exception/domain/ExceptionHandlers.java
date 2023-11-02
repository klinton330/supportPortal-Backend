package com.support.supportPortal.exception.domain;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.support.supportPortal.domain.HttpResponse;
import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Objects;


@RestControllerAdvice
public class ExceptionHandlers implements ErrorController {

    private final Logger logger= LoggerFactory.getLogger(getClass());
    private static final String ACCOUNT_LOCKED="Your account had been locked .Please contact administration";
    private static final String METHOD_IS_NOT_ALLOWED="This request method is not allowed on this end point.Please send a '%s' request";
    private static final String INTERNAL_SERVER_ERROR_MSG="An error occured while processing the request";
    private static final String INCORRECT_CREDENTIALS="Username/password incorrect.Please try again";
    private static final String ACCOUNT_DISABLED="Your account has been disabled.If this is an error ,Please contact Administration";
    private static final String ERROR_PROCESSING_FILE="Error occurred while processing file";
    private static final String NOT_ENOUGH_PERMISSION="You do not have enough permission";
    //Handles the white label error page
    private static final String ERROR_PATH="/error";

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException(){
        return createHttpExceptionResponse(HttpStatus.BAD_REQUEST,ACCOUNT_DISABLED);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialException(){
        return createHttpExceptionResponse(HttpStatus.BAD_REQUEST,INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(){
        return createHttpExceptionResponse(HttpStatus.FORBIDDEN,NOT_ENOUGH_PERMISSION);
    }
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse>lockedException(){
        return createHttpExceptionResponse(HttpStatus.UNAUTHORIZED,ACCOUNT_LOCKED);
    }
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse>tokenexpiredException(TokenExpiredException e){
        return createHttpExceptionResponse(HttpStatus.UNAUTHORIZED,e.getMessage().toUpperCase());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse>emailExistException(EmailExistException e){
        return createHttpExceptionResponse(HttpStatus.BAD_REQUEST,e.getMessage().toUpperCase());
    }
    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse>usernameExistException(UsernameExistException e){
        return createHttpExceptionResponse(HttpStatus.BAD_REQUEST,e.getMessage().toUpperCase());
    }
    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse>emailNotFoundException(EmailNotFoundException e){
        return createHttpExceptionResponse(HttpStatus.BAD_REQUEST,e.getMessage().toUpperCase());
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse>usernameNotFoundException(UserNotFoundException e){
        return createHttpExceptionResponse(HttpStatus.BAD_REQUEST,e.getMessage().toUpperCase());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception e){
        e.printStackTrace();
        return createHttpExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse>methodNotSupportedException(HttpRequestMethodNotSupportedException e){
        HttpMethod supportedMethod= Objects.requireNonNull(e.getSupportedHttpMethods()).iterator().next();
        return createHttpExceptionResponse(HttpStatus.METHOD_NOT_ALLOWED,String.format(METHOD_IS_NOT_ALLOWED,supportedMethod));
    }
    @ExceptionHandler(NoResultException.class)
   public ResponseEntity<HttpResponse>notFoundException(NoResultException exception){
       return createHttpExceptionResponse(HttpStatus.NOT_FOUND,exception.getMessage());
   }

   @ExceptionHandler(IOException.class)
   public ResponseEntity<HttpResponse>IOException(IOException exception){
        return createHttpExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR,ERROR_PROCESSING_FILE);
   }

   /* @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse>noHandlerException(NoHandlerFoundException exception){
        return createHttpExceptionResponse(HttpStatus.BAD_REQUEST,exception.getMessage());
    }*/

    private ResponseEntity<HttpResponse> createHttpExceptionResponse(HttpStatus httpStatus,String message){
        HttpResponse httpResponse=new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(),message.toUpperCase());
        return new ResponseEntity<>(httpResponse,httpStatus);
    }



    public String getErrorPath() {
        return ERROR_PATH;
    }
    //Handles the white label error page
    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse>notFound404(){
        return createHttpExceptionResponse(HttpStatus.NOT_FOUND,"There is no mapping for this url");
    }

}
