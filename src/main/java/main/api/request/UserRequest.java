package main.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
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
