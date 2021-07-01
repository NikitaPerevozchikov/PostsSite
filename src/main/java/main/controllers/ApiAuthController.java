package main.controllers;

import java.util.Map;
import main.service.AuthorizationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

  private AuthorizationService authorizationService;

  public ApiAuthController(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  @GetMapping("/check")
  public Map<String, Object> getCheckAuthorization() {
    return authorizationService.getCheckAuthorization();
  }
}
