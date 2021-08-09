package main.controllers;

import java.security.Principal;
import main.api.request.AuthorizationRequest;
import main.api.request.PasswordRequest;
import main.api.request.UserRequest;
import main.api.response.AuthorizationResponse;
import main.api.response.CaptchaResponse;
import main.api.response.UserResponse;
import main.service.AuthorizationService;
import main.service.CaptchaService;
import main.service.UserService;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/check")
  public ResponseEntity<?> getCheckAuthorization(Principal principal) {
    return authorizationService.getCheckAuthorization(principal);
  }

  @GetMapping("/captcha")
  public ResponseEntity<?> addCaptcha() {
    return captchaService.addCaptcha();
  }

  @PostMapping("/register")
  public ResponseEntity<?> addUser(@RequestBody UserRequest userRequest) {
    return userService.addUser(userRequest);
  }

  @PostMapping("/login")
  public ResponseEntity<?> addAuthorizationUser(
      @RequestBody AuthorizationRequest authorizationRequest) {
    return authorizationService.addAuthorizationUser(authorizationRequest);
  }

  @GetMapping("/logout")
  public ResponseEntity<?> deleteAuthorizationUser() {
    return authorizationService.deleteAuthorizationUser();
  }

  @PostMapping("/restore")
  public ResponseEntity<?> recoveryPassword(@RequestBody UserRequest request) {
    return userService.recoveryPassword(request);
  }

  @PostMapping("/password")
  public ResponseEntity<?> updatePassword(@RequestBody PasswordRequest request) {
    return userService.updatePassword(request);
  }



}
