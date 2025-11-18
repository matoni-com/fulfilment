package com.example.fulfilment.controller.dto;

/**
 * @param token_type almost always "Bearer"
 * @param expires_in in seconds
 */
public record AuthenticateUserResponse(String access_token, String token_type, int expires_in) {}
