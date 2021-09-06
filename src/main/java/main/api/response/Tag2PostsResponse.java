package main.api.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class Tag2PostsResponse {

  List<Tag2Posts> tags;

  public interface Tag2Posts {

    String getName();

    double getWeight();
  }
}
