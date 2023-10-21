package com.support.supportPortal.constant;

public class SecurityConstant {

    public static final long EXPIRATION_TIME=432_000_000;//5 DAYS
    public static final String TOKEN_PREFIX="Bearer";
    public static final String JWT_TOKEN_HEADER="Jwt_Token";
    public static final String TOKEN_CANNOT_BE_VERIFIED="Token cannot be verified";
    public static final String GET_ARRAY_LLC="Get Array,LLC";
    public static final String GET_ARRAY_ADMINISTARTION="User Management Portal";
    public static final String AUTHORITIES="Authorities";
    public static final String FORBIDDEN_MESSAGE="You need to login into access this page";
    public static final String ACCESS_DENIED_MESSAGE="you do not have permission to access this page";
    public static final String OPTION_HTTP_METHOD="OPTIONS";
    public static final String[] PUBLIC_URLS={"/user/login","/user/register","/user/resetpassword/**","/user/image/**"};

}
