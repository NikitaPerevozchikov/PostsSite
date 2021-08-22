package main.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class UserUpdateRequest {

  @JsonProperty("e_mail")
  private String email;

  private String password;
  private String name;
  private Byte removePhoto;
}
