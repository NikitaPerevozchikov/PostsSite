package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Getter
@Setter
@ToString
public class UserRequest {

  @JsonProperty("e_mail")
  private String email;

  private String password;
  private String name;
  private String captcha;

  @JsonProperty("captcha_secret")
  private String captchaSecret;

  private MultipartFile photo;
  private Integer removePhoto;
}
