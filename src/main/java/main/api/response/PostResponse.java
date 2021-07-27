package main.api.response;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class PostResponse {

  boolean active;
  private int id;
  private long timestamp;
  private UserPost user;
  private String title;
  private String text;
  private int likeCount;
  private int dislikeCount;
  private int viewCount;
  private List<Comment> comments = new ArrayList<>();
  private String[] tags;

  @Getter
  @Setter
  @AllArgsConstructor
  public static class UserPost {

    private int id;
    private String name;
  }

  @Setter
  @Getter
  @AllArgsConstructor
  public static class Comment {

    private Integer id;
    private long timestamp;
    private String text;
    private UserComment user;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserComment {

      private int id;
      private String name;
      private String photo;
    }
  }
}
