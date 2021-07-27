package main.controllers;

import java.security.Principal;
import main.api.request.AuthorizationRequest;
import main.api.request.UserRequest;
import main.api.response.AuthorizationResponse;
import main.api.response.CaptchaResponse;
import main.api.response.RegisterResponse;
import main.service.AuthorizationService;
import main.service.CaptchaService;
import main.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

  private AuthorizationService authorizationService;
  private UserService userService;
  private CaptchaService captchaService;

  public ApiAuthController(
      AuthorizationService authorizationService,
      UserService userService,
      CaptchaService captchaService) {
    this.authorizationService = authorizationService;
    this.userService = userService;
    this.captchaService = captchaService;
  }

  @PostMapping("/register")
  public RegisterResponse createUser(@RequestBody UserRequest userRequest) {
    return userService.createUser(userRequest);
  }

  @GetMapping("/check")
  public AuthorizationResponse getCheckAuthorization(Principal principal) {
    return authorizationService.getCheckAuthorization(principal);
  }

  @PostMapping("/login")
  public AuthorizationResponse createAuthorizationUser(
      @RequestBody AuthorizationRequest authorizationRequest) {
    return authorizationService.createAuthorizationUser(authorizationRequest);
  }

  @GetMapping("/logout")
  public AuthorizationResponse deleteAuthorizationUser(Principal principal) {
    return authorizationService.deleteAuthorizationUser(principal);
  }

  @GetMapping("/captcha")
  public CaptchaResponse getCaptcha() {
    return captchaService.createCaptcha();
  }


}
