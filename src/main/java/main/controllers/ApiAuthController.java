package main.controllers;

import java.security.Principal;
import main.api.request.AuthorizationRequest;
import main.api.request.EmailRequest;
import main.api.request.PasswordRequest;
import main.api.request.UserRequest;
import main.api.response.AuthorizationResponse;
import main.api.response.CaptchaResponse;
import main.api.response.UserResponse;
import main.service.AuthorizationService;
import main.service.CaptchaService;
import main.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

  private final AuthorizationService authorizationService;
  private final UserService userService;
  private final CaptchaService captchaService;

  public ApiAuthController(
      AuthorizationService authorizationService,
      UserService userService,
      CaptchaService captchaService) {
    this.authorizationService = authorizationService;
    this.userService = userService;
    this.captchaService = captchaService;
  }

  @GetMapping("/check")
  @ResponseStatus(value = HttpStatus.OK)
  public AuthorizationResponse getCheckAuthorization(Principal principal) {
    return authorizationService.getCheckAuthorization(principal);
  }

  @GetMapping("/captcha")
  public CaptchaResponse addCaptcha() {
    return captchaService.addCaptcha();
  }

  @PostMapping("/register")
  @ResponseStatus(value = HttpStatus.OK)
  public UserResponse addUser(@RequestBody UserRequest userRequest) {
    return userService.addUser(userRequest);
  }

  @PostMapping("/login")
  @ResponseStatus(value = HttpStatus.OK)
  public AuthorizationResponse addAuthorizationUser(
      @RequestBody AuthorizationRequest authorizationRequest) {
    return authorizationService.addAuthorizationUser(authorizationRequest);
  }

  @GetMapping("/logout")
  @ResponseStatus(value = HttpStatus.OK)
  public AuthorizationResponse deleteAuthorizationUser() {
    return authorizationService.deleteAuthorizationUser();
  }

  @PostMapping("/restore")
  @ResponseStatus(value = HttpStatus.OK)
  public UserResponse recoveryPassword(@RequestBody EmailRequest request) {
    return userService.recoveryPassword(request);
  }

  @PostMapping("/password")
  @ResponseStatus(value = HttpStatus.OK)
  public UserResponse updatePassword(@RequestBody PasswordRequest request) {
    return userService.updatePassword(request);
  }
}
