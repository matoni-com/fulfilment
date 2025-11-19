package com.example.fulfilment.entity;

public record FtpConnection( String host, String username, String password, Integer port) implements ConnectionSettings {}