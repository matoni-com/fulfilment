package com.example.fulfilment.entity;

public record UsernamePasswordConnection(String username, String password, String url)
    implements ConnectionSettings {}
