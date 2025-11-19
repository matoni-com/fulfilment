package com.example.fulfilment.entity;


public record ApiKeyConnection(String apiKey, String url) implements ConnectionSettings {}