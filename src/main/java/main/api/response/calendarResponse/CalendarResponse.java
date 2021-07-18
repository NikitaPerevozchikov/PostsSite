package main.api.response.calendarResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CalendarResponse {

  private Set<String> years = new HashSet<>();
  private Map<String, Integer> posts = new HashMap<>();
}
