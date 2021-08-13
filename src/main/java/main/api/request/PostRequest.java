package main.api.request;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PostRequest {
  private long timestamp;
  private int active;
  private String title;
  private Set<String> tags = new HashSet<>();
  private String text;
}
