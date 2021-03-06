package com.codeworrisors.Movie_Community_Web.property;

public interface JwtProperties {
    String SECRET = "movie"; // 서버만 알고 있는 SECRET 키
    int EXPIRATION_TIME = 60000*300; // 10분
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}