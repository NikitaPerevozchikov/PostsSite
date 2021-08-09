package main.api.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class GlobalSettingsRequest {

  @JsonProperty("MULTIUSER_MODE")
  private Boolean multiuserMode;

  @JsonProperty("POST_PREMODERATION")
  private Boolean postPremoderation;

  @JsonProperty("STATISTICS_IS_PUBLIC")
  private Boolean statisticsIsPublic;
}
