package com.support.supportPortal.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttempService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTS=5;
    private static final int ATTEMPT_INCREMENT=1;
    private LoadingCache<String,Integer>loginAttemptCache;

    public LoginAttempService(){
        super();
        loginAttemptCache= CacheBuilder.newBuilder()
                //Cache will Expire at 15 mins
                .expireAfterWrite(15, TimeUnit.MINUTES)
                //Totally 100 Number of entries allowed in cache
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }



    //This method remove the user from cache.
    public void evictUserFromLoginAttemptCache(String username){
        loginAttemptCache.invalidate(username);
    }
   //This method add user to the cache
    public void addUserToLoginAttemptCache(String username){
        int attempts=0;
        try {
            attempts=ATTEMPT_INCREMENT+loginAttemptCache.get(username);
            loginAttemptCache.put(username,attempts);
        }
         catch (ExecutionException e) {
          e.printStackTrace();
        }
    }

    //This method returns true if users exceed his login attempt
    public boolean hasExceededMaxAttempts(String username)  {
        try {
            return loginAttemptCache.get(username)>=MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

}
