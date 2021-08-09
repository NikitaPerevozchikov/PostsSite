package main.api.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
public class StatisticResponse {
  private int postsCount;
  private int likesCount;
  private int dislikesCount;
  private int viewsCount;
  private long firstPublication;
}
