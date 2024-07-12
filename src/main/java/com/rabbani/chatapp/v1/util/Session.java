package com.rabbani.chatapp.v1.util;

import com.rabbani.chatapp.v1.repository.AccessModuleRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class Session implements InitializingBean {

    private static final String BEARER_PREFIX = "Bearer ";

    public static final String COOKIE_SESSION_TOKEN_NAME = "_session_token";

    public static final String COOKIE_SESSION_REFRESH_TOKEN_NAME = "_session_refresh_token";

    private final HttpServletRequest request;

    private final RedisOperations<String, UserSession.RedisUserSession> redisUserSessionRedisOperations;

    private final AccessModuleRepository accessModuleRepository;

    private final CacheManager.Cache<String,UserSession>  sessionCache;

    private final Map<String,AuthQualifier> authQualifiers = new HashMap<>();

    public UserSession getSession() {
        String auth =  request.getHeader(HttpHeaders.AUTHORIZATION);
        String token = null;
        if(!StringUtils.isEmpty(auth) && auth.length() > BEARER_PREFIX.length() && auth.startsWith(BEARER_PREFIX)){
            token = auth.substring(BEARER_PREFIX.length());
        }
        else{
            if(request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals(COOKIE_SESSION_TOKEN_NAME)) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if(token == null) {
            throw new ResponseException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        UserSession userSession = sessionCache.get(token,_currentSession->_currentSession == null || _currentSession.getExpiresAt() > System.currentTimeMillis(),(_token)->redisUserSessionRedisOperations.opsForValue().get(RedisPrefix.token.serialize(_token)));
        if(userSession == null){
            sessionCache.remove(token);
        }
        return userSession;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        accessModuleRepository.findAllAccessModules()
                .forEach(accessModuleSessionQuery ->
                        authQualifiers.put(accessModuleSessionQuery.getCode(),new AuthQualifier(accessModuleSessionQuery.getQualifier()/64,1L << (accessModuleSessionQuery.getQualifier()%64))));

    }

    public Validator auth(String... codes){
        Map<Integer,Long> authQualifiers = new HashMap<>();
        for(String code : codes){
            AuthQualifier authQualifier = this.authQualifiers.get(code);
            if(authQualifier == null){
                throw new RuntimeException("Invalid '"+code+"' no module found for given code");
            }
            long prev = authQualifiers.getOrDefault(authQualifier.index,0l);
            authQualifiers.put(authQualifier.index,prev|authQualifier.bitMask);
        }

        return ()->{
            UserSession currentSession = getSession();
            if (currentSession == null){
                throw new ResponseException(HttpStatus.UNAUTHORIZED,"Unauthorized");
            }

            if(!authQualifiers.isEmpty()){
                boolean isAuthorized = false;
               for(Map.Entry<Integer,Long> authQualifier: authQualifiers.entrySet()){
                   Long[] userAccess = currentSession.getQualifier();
                   if(userAccess == null || userAccess.length <= authQualifier.getKey() || userAccess[authQualifier.getKey()] == null){
                       continue;
                   }

                   long access = userAccess[authQualifier.getKey()];

                   if((access & authQualifier.getValue().longValue()) > 0L){
                       isAuthorized = true;
                       break;
                   }
               }

               if(!isAuthorized){
                   throw new ResponseException(HttpStatus.FORBIDDEN,"Forbidden");
               }
            }
        };
    }

    @AllArgsConstructor
    private static class AuthQualifier{
        Integer index;
        Long bitMask;
    }

    public interface Validator{
        void validate();
    }

}
