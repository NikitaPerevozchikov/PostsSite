package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsResponse {

  @JsonProperty("MULTIUSER_MODE")
  private boolean multiuserMode;

  @JsonProperty("POST_PREMODERATION")
  private boolean postPremoderation;

  @JsonProperty("STATISTICS_IS_PUBLIC")
  private boolean statisticsIsPublic;
}

