package main.api.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PostsResponse {

  private int count;
  private List<Post> posts;

  @Getter
  @Setter
  public static class Post {

    private int id;
    private long timestamp;
    private User user;
    private String title;
    private String announce;
    private int viewCount;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;

    @Getter
    @Setter
    public static class User {
      private int id;
      private String name;

      public User(int id, String name) {
        this.id = id;
        this.name = name;
      }
    }
  }
}
