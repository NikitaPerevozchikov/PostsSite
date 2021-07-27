package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@JsonInclude(Include.NON_EMPTY)
public class RegisterResponse {

  private boolean result = false;
  private Map<String, String> errors = new HashMap<>();
}
