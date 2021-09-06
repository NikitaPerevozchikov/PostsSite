package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class AuthorizationResponse {
  private boolean result = false;

  @JsonProperty("user")
  private UserResponse userResponse;

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserResponse {
    private int id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
  }
}
