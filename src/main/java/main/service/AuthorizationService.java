package main.service;

import java.util.HashMap;
import java.util.Map;
import main.models.User;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

  public Map<String, Object> getCheckAuthorization() {
    Map<String, Object> response = new HashMap<>();
    boolean result = false;
    response.put("result", result);
    if (result) {
      User user = new User();
      response.put("user", user);
    }
    return response;
  }
}