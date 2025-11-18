package com.example.fulfilment.service.dto;

/**
 * @param token_type almost always "Bearer"
 * @param expires_in in seconds
 */
public record AuthenticateUserResult(String access_token, String token_type, int expires_in) {}
