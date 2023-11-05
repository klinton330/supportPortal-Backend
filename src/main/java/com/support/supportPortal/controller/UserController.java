package com.support.supportPortal.controller;

import com.support.supportPortal.constant.SecurityConstant;
import com.support.supportPortal.domain.HttpResponse;
import com.support.supportPortal.domain.User;
import com.support.supportPortal.domain.UserPrincipal;
import com.support.supportPortal.exception.domain.EmailExistException;
import com.support.supportPortal.exception.domain.EmailNotFoundException;
import com.support.supportPortal.exception.domain.ExceptionHandlers;
import com.support.supportPortal.exception.domain.UsernameExistException;
import com.support.supportPortal.repository.UserRepository;
import com.support.supportPortal.service.UserService;
import com.support.supportPortal.utility.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.support.supportPortal.constant.FileConstant.*;
import static com.support.supportPortal.constant.UserConstant.PROFILE_PICTURE_UPDATED;
import static com.support.supportPortal.constant.UserConstant.USER_DELETED_SUCCESSFULLY;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

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
        return new ResponseEntity<>(registeduser, OK);

    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User user) {
        authenticate(user.getUsername(),user.getPassword());
        User getUser=userRepository.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal=new UserPrincipal(getUser);
        HttpHeaders httpHeaders=getJwtHeader(userPrincipal);
        return new ResponseEntity<>(getUser,httpHeaders, OK);

    }


    @PostMapping("/adduser")
    public  ResponseEntity<User> addNewUser(@RequestParam("firstName")String firstName,
                                            @RequestParam("lastName")String lastName,
                                            @RequestParam("username")String username,
                                            @RequestParam("email")String email,
                                            @RequestParam("role")String role,
                                            @RequestParam("isActive")String isActive,
                                            @RequestParam("isNonLocked")String isNonLocked,
                                            @RequestParam(value = "profileImage", required = false)MultipartFile profileImage) throws EmailExistException, IOException, UsernameExistException {
        User newUser=userService.addNewUser(firstName,lastName,username,email,role,Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<>(newUser,OK);

    }
    @PostMapping("/updateuser")
    public  ResponseEntity<User> updateCurerntUser(@RequestParam("currentUserName")String currentUserName,@RequestParam("firstName")String firstName,
                                            @RequestParam("lastName")String lastName,
                                            @RequestParam("username")String username,
                                            @RequestParam("email")String email,
                                            @RequestParam("role")String role,
                                            @RequestParam("isActive")String isActive,
                                            @RequestParam("isNonLocked")String isNonLocked,
                                            @RequestParam(value = "profileImage", required = false)MultipartFile profileImage) throws EmailExistException, IOException, UsernameExistException {
        User updatedUser=userService.updateUser(currentUserName,firstName,lastName,username,email,role,Boolean.parseBoolean(isNonLocked),Boolean.parseBoolean(isActive),profileImage);
        return new ResponseEntity<>(updatedUser,OK);

    }

    @GetMapping("/find/{username}")
    public ResponseEntity<User>getUser(@PathVariable("username")String username){
        User user=userService.findUserByUsername(username);
        return new ResponseEntity<>(user,OK);
    }

    @GetMapping("/list")
    public ResponseEntity <List<User>>getAllUsers(){
        List<User>users=userService.getUser();
        return new ResponseEntity<>(users,OK);

    }
    @GetMapping("/resetpassword/{email}")
    public ResponseEntity <HttpResponse> resetPassword(@PathVariable("email")String email) throws EmailNotFoundException {
        userService.resetPassword(email);
        return response(OK,"Email sent successfully:"+email);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResponseEntity<HttpResponse>deleteUser(@PathVariable("id")long id){
        userService.deleteUser(id);
        return response(NO_CONTENT,USER_DELETED_SUCCESSFULLY);
    }

    @PostMapping("/updateProfileImage")
    public ResponseEntity<User>updateProfileImage(@RequestParam("username")String username,@RequestParam(value = "profileImage")MultipartFile profileImage) throws EmailExistException, IOException, UsernameExistException {
        User updatedProfileImage=userService.updateUserProfileImage(username,profileImage);
        return new ResponseEntity<>(updatedProfileImage,OK);
    }
    //Reading the Image
    @GetMapping(path = "/image/{username}/{filename}",produces = {IMAGE_JPEG_VALUE})
    public byte[] getProfileImage(@PathVariable ("username") String username,@PathVariable("filename")String filename) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER+username+FORWARD_SLASH+filename));
    }

    @GetMapping(path = "/image/profile/{username}",produces = {IMAGE_JPEG_VALUE})
    public byte[] getTempProfileImage(@PathVariable ("username") String username,@PathVariable("filename")String filename) throws IOException {
        URL url=new URL(TEMP_PROFILE_IMAGE_BASE_URL+username);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        try(InputStream inputStream=url.openStream()){
            int byteStream;
            byte[] chunk=new byte[1024];
            while ((byteStream=inputStream.read(chunk))>0){
                byteArrayOutputStream.write(chunk,0,byteStream);
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase().toUpperCase(),message.toUpperCase()),httpStatus);
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
