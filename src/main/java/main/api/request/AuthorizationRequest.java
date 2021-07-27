package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AuthorizationRequest {

  @JsonProperty("e_mail")
  private String email;

  private String password;
}
