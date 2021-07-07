package main.api.response;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PostsResponse {

  private int count;
  private List<Post> posts = new ArrayList<>();

  @Getter
  @Setter
  public static class Post {

    private int id;
    private long timestamp;
    private User user;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;

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
