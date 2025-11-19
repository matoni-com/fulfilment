package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.AuthenticateUserRequest;
import com.example.fulfilment.controller.dto.AuthenticateUserResponse;
import com.example.fulfilment.service.UserAuthenticationService;
import com.example.fulfilment.service.dto.AuthenticateUserCommand;
import com.example.fulfilment.service.dto.AuthenticateUserResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class UserAuthenticationController {

  private AuthenticateUserMapper authenticateUserMapper;
  private UserAuthenticationService service;

  @PreAuthorize("permitAll()")
  @PostMapping("/authenticate")
  @ResponseStatus(HttpStatus.OK)
  public AuthenticateUserResponse authenticate(@RequestBody AuthenticateUserRequest request)
      throws AuthenticationException {

    AuthenticateUserCommand command = authenticateUserMapper.toCommand(request);
    AuthenticateUserResult result = service.authenticate(command);

    return authenticateUserMapper.toResponse(result);
  }
}
