package com.support.supportPortal.service;

import com.support.supportPortal.constant.EmailConstant;
import org.springframework.stereotype.Service;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
public class EmailService {
    public void send(final String password, String to, String sub, String msg) {

        // Get properties object
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        // get Session
        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConstant.USERNAME, EmailConstant.PASSWORD);
            }
        });
        // compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(sub);
            message.setText(msg);
            // send message
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPassWordToEmail(String password, String to,String firstName){
        String sub=EmailConstant.EMAIL_SUBJECT;
        String msg="Hello "+firstName+",\n\n Your New Account Password is: "+password+ "\n\n The Support Team" ;
        send(password,to,sub,msg);
    }
}
