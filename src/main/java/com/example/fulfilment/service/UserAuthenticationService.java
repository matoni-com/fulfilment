package com.example.fulfilment.service;

import com.example.fulfilment.service.dto.AuthenticateUserCommand;
import com.example.fulfilment.service.dto.AuthenticateUserResult;
import org.springframework.security.core.AuthenticationException;

public interface UserAuthenticationService {
  AuthenticateUserResult authenticate(AuthenticateUserCommand authenticateUserCommand)
      throws AuthenticationException;
}
