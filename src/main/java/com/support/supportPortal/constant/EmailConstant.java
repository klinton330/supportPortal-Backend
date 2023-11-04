package com.support.supportPortal.constant;

public class EmailConstant {

    public static final String USERNAME="klintonaws@gmail.com";
    public static final String PASSWORD="junxhnujbdevlbis";
    public static final String EMAIL_SUBJECT="Password for SupportPortal";
    public static final String HOST="mail.smtp.host";
    public static final String HOST_NAME="smtp.gmail.com";
    public static final String SOCKET_FACTORY_PORT="mail.smtp.socketFactory.port";
    public static final String SOCKET_FACTORY_PORT_NO="465";
    public static final String SOCKET_FACTORY_CLASS= "mail.smtp.socketFactory.class";
    public static final String SOCKET_FACTORY_CLASS_NAME="javax.net.ssl.SSLSocketFactory";

    public static final String SMTP_OAUTH="mail.smtp.auth";

    public static final String SMTP_OAUTH_ENABLELD="true";
    public static final String SMTP_PORT="mail.smtp.port";
    public static final String SMTP_PORT_NO="465";

}




/*
*    Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
* */