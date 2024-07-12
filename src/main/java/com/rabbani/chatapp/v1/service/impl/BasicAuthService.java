package com.rabbani.chatapp.v1.service.impl;

import com.rabbani.chatapp.v1.configuration.properties.ApplicationProperties;
import com.rabbani.chatapp.v1.dto.AuthControllerDto;
import com.rabbani.chatapp.v1.dto.Response;
import com.rabbani.chatapp.v1.entity.query.UserAuthQuery;
import com.rabbani.chatapp.v1.repository.UserRepository;
import com.rabbani.chatapp.v1.service.AuthService;
import com.rabbani.chatapp.v1.util.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.random.RandomGenerator;

@Service
@AllArgsConstructor
public class BasicAuthService implements AuthService {

    private static final String BASIC_PREFIX = "Basic ";

    private final RedisOperations<String, UserSession.RedisUserSession> redisSessionRedisOperations;

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final RandomGenerator secureRandom;

    private final ApplicationProperties applicationProperties;

    private final Session session;

    private final CacheManager.Cache<String,UserSession>  sessionCache;

    @Override
    public Response<AuthControllerDto.SignInResponse> signIn(HttpServletRequest request, HttpServletResponse response) {
        String text = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(text) || text.length() <= BASIC_PREFIX.length() || !text.startsWith(BASIC_PREFIX)) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, "Missing authorization header(basic)");
        }

        String value = text.substring(BASIC_PREFIX.length());
        String[] userPassword = new String(Base64.getDecoder().decode(value.getBytes())).split(":");

        if (userPassword.length != 2) {
            throw new ResponseException(HttpStatus.BAD_REQUEST, "Invalid authorization header(basic)");
        }

        UserAuthQuery userEntity = userRepository.findByEmail(userPassword[0]).orElseThrow(() -> new ResponseException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (!passwordEncoder.matches(userPassword[1], userEntity.getPassword())) {
            throw new ResponseException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        byte[] data = new byte[35];
        String token;
        String refreshToken;
        String prefixedToken;
        String prefixedRefreshToken;
        do {
            secureRandom.nextBytes(data);
            token = Base64.getUrlEncoder().encodeToString(data);
            prefixedToken = RedisPrefix.token.serialize(token);
            if (!redisSessionRedisOperations.hasKey(prefixedToken)) {
                break;
            }
        }
        while (true);

        do {
            secureRandom.nextBytes(data);
            refreshToken = Base64.getUrlEncoder().encodeToString(data);
            prefixedRefreshToken = RedisPrefix.refreshToken.serialize(refreshToken);
            if (!redisSessionRedisOperations.hasKey(prefixedRefreshToken)) {
                break;
            }
        }
        while (true);

        UserSession.RedisUserSession session = new UserSession.RedisUserSession();
        session.setId(userEntity.getId());
        session.setEmail(userEntity.getEmail());
        session.setFirstName(userEntity.getFirstName());
        session.setLastName(userEntity.getLastName());
        session.setQualifier(userEntity.getRoleQualifier());
        session.setRefreshToken(token);
        session.setRefreshToken(refreshToken);
        session.setExpiresAt(System.currentTimeMillis()+applicationProperties.getSession().getTokenTtl().toMillis());

        AuthControllerDto.SignInResponse responsePayload = new AuthControllerDto.SignInResponse();
        responsePayload.setToken(token);
        responsePayload.setRefreshToken(refreshToken);
        responsePayload.setExpiresIn(applicationProperties.getSession().getTokenTtl().toSeconds());


        redisSessionRedisOperations.opsForValue().set(prefixedToken, session);
        redisSessionRedisOperations.expire(prefixedToken, applicationProperties.getSession().getTokenTtl());

        redisSessionRedisOperations.opsForValue().set(prefixedRefreshToken, session);
        redisSessionRedisOperations.expire(prefixedRefreshToken, applicationProperties.getSession().getRefreshTokenTtl());

        Cookie sessionTokenCookie = new Cookie(Session.COOKIE_SESSION_TOKEN_NAME, token);
        sessionTokenCookie.setMaxAge(((int) applicationProperties.getSession().getTokenTtl().toSeconds()) - 3);
        sessionTokenCookie.setPath("/");
        response.addCookie(sessionTokenCookie);

        Cookie refreshTokenCookie = new Cookie(Session.COOKIE_SESSION_REFRESH_TOKEN_NAME, refreshToken);
        refreshTokenCookie.setMaxAge(((int) applicationProperties.getSession().getRefreshTokenTtl().toSeconds()) - 3);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
        return new Response<>(responsePayload);
    }

    @Override
    public Response<AuthControllerDto.RefreshSessionResponse> refreshSession(AuthControllerDto.RefreshSessionRequest requestPayload, HttpServletRequest request, HttpServletResponse response) {
        String lookupToken = "";
        if (requestPayload != null && !StringUtils.isEmpty(requestPayload.getRefreshToken())) {
            lookupToken = requestPayload.getRefreshToken();
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(Session.COOKIE_SESSION_REFRESH_TOKEN_NAME)) {
                        lookupToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        UserSession oldSession = redisSessionRedisOperations.opsForValue().get(RedisPrefix.refreshToken.serialize(lookupToken));
        if (oldSession == null) {
            throw new ResponseException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        byte[] data = new byte[35];
        String token;
        String refreshToken;
        String prefixedToken;
        String prefixedRefreshToken;
        do {
            secureRandom.nextBytes(data);
            token = Base64.getEncoder().encodeToString(data);
            prefixedToken = RedisPrefix.token.serialize(token);
            if (!redisSessionRedisOperations.hasKey(prefixedToken)) {
                break;
            }
        }
        while (true);

        do {
            secureRandom.nextBytes(data);
            refreshToken = Base64.getEncoder().encodeToString(data);
            prefixedRefreshToken = RedisPrefix.refreshToken.serialize(refreshToken);
            if (!redisSessionRedisOperations.hasKey(prefixedRefreshToken)) {
                break;
            }
        }
        while (true);


        UserAuthQuery userEntity = userRepository.findById(oldSession.getId()).orElseThrow(() -> new ResponseException(HttpStatus.UNAUTHORIZED, "User does not exists"));
        UserSession.RedisUserSession session = new UserSession.RedisUserSession();
        session.setId(userEntity.getId());
        session.setEmail(userEntity.getEmail());
        session.setFirstName(userEntity.getFirstName());
        session.setLastName(userEntity.getLastName());
        session.setQualifier(userEntity.getRoleQualifier());
        session.setRefreshToken(token);
        session.setRefreshToken(refreshToken);
        session.setExpiresAt(System.currentTimeMillis()+applicationProperties.getSession().getTokenTtl().toMillis());

        AuthControllerDto.RefreshSessionResponse responsePayload = new AuthControllerDto.RefreshSessionResponse();
        responsePayload.setToken(token);
        responsePayload.setRefreshToken(refreshToken);
        responsePayload.setExpiresIn(applicationProperties.getSession().getTokenTtl().toSeconds());

        redisSessionRedisOperations.opsForValue().set(prefixedToken, session);
        redisSessionRedisOperations.expire(prefixedToken, applicationProperties.getSession().getTokenTtl());

        redisSessionRedisOperations.opsForValue().set(prefixedRefreshToken, session);
        redisSessionRedisOperations.expire(prefixedRefreshToken, applicationProperties.getSession().getRefreshTokenTtl());

        redisSessionRedisOperations.delete(RedisPrefix.token.serialize(oldSession.getToken()));
        redisSessionRedisOperations.delete(RedisPrefix.refreshToken.serialize(oldSession.getRefreshToken()));

        sessionCache.remove(token);
        Cookie sessionTokenCookie = new Cookie(Session.COOKIE_SESSION_TOKEN_NAME, token);
        sessionTokenCookie.setPath("/");
        sessionTokenCookie.setMaxAge(((int) applicationProperties.getSession().getTokenTtl().toSeconds()) - 3);
        response.addCookie(sessionTokenCookie);

        Cookie refreshTokenCookie = new Cookie(Session.COOKIE_SESSION_REFRESH_TOKEN_NAME, refreshToken);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(((int) applicationProperties.getSession().getRefreshTokenTtl().toSeconds()) - 3);
        response.addCookie(refreshTokenCookie);
        refreshTokenCookie.toString();
        return new Response<>(responsePayload);
    }

    @Override
    public Response<?> signOut(HttpServletRequest request,HttpServletResponse response) {
        UserSession userSession = session.getSession();
        redisSessionRedisOperations.delete(RedisPrefix.token.serialize(userSession.getToken()));
        redisSessionRedisOperations.delete(RedisPrefix.refreshToken.serialize(userSession.getRefreshToken()));

        Cookie sessionTokenCookie = new Cookie(Session.COOKIE_SESSION_TOKEN_NAME, "");
        sessionTokenCookie.setMaxAge(0);
        sessionTokenCookie.setPath("/");
        response.addCookie(sessionTokenCookie);

        Cookie refreshTokenCookie = new Cookie(Session.COOKIE_SESSION_REFRESH_TOKEN_NAME, "");
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);
        return new Response<>();
    }
}
