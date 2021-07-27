package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class UserRequest {

  @JsonProperty("e_mail")
  private String email;

  private String password;
  private String name;
  private String captcha;

  @JsonProperty("captcha_secret")
  private String captchaSecret;
}
