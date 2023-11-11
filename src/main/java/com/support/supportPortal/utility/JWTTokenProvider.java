package com.support.supportPortal.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.support.supportPortal.constant.SecurityConstant;
import com.support.supportPortal.domain.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Component
public class JWTTokenProvider {

    @Value("${jwt.secret}")
    private String secret;
    
    public String generateJwtToken(UserPrincipal userPrincipal){
        String[] claims=getClaimFromUser(userPrincipal);
        return JWT.create()
                .withIssuer(SecurityConstant.GET_ARRAY_LLC)
                .withAudience(SecurityConstant.GET_ARRAY_ADMINISTARTION)
                .withIssuedAt(new Date())
                .withSubject(userPrincipal.getUsername())
                .withArrayClaim(SecurityConstant.AUTHORITIES,claims)
                .withExpiresAt(new Date(System.currentTimeMillis()+SecurityConstant.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public List<GrantedAuthority> getAuthorities(String token){
        String[] claims=getClaimFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    public Authentication getAuthentication(String username, List<GrantedAuthority>authorities, HttpServletRequest httpServletRequest){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=
                new UsernamePasswordAuthenticationToken(username,null,authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(String username,String token){
        JWTVerifier verifier=getJWTVerifier();
        return StringUtils.isNotEmpty(username)&& !isTokenExpired(verifier,token);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration=verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    public String getSubject(String token){
        JWTVerifier verifier=getJWTVerifier();
        return verifier.verify(token).getSubject();
    }

    private String[] getClaimFromToken(String token) {
        JWTVerifier verifier=getJWTVerifier();
        return verifier.verify(token)
                .getClaim(SecurityConstant.AUTHORITIES)
                .asArray(String.class);
    }

    //JWT Verifier basically loads the which type of algorithms we using
    private JWTVerifier getJWTVerifier() {
        JWTVerifier jwtVerifier = null;
        try{
            Algorithm algorithm=Algorithm.HMAC512(secret);
            jwtVerifier=JWT.require(algorithm)
                           .withIssuer(SecurityConstant.GET_ARRAY_LLC)
                            .build();


        }catch (JWTVerificationException jwtVerificationException){
               throw new JWTVerificationException(SecurityConstant.TOKEN_CANNOT_BE_VERIFIED);
        }
        return jwtVerifier;
    }

    private String[] getClaimFromUser(UserPrincipal userPrincipal) {
        List<String>authorities=new ArrayList<>();
        for(GrantedAuthority grantedAuthority: userPrincipal.getAuthorities()){
            authorities.add(grantedAuthority.getAuthority());
        }
        return authorities.toArray(new String[0]);
        //new String[0]: This creates a new, empty String array with a length of 0.
        // This is used as an argument to the toArray method.
        // The purpose of this argument is to specify the type of the resulting array. In this case, it's a String array.
    }
}
