package main.api.response.postsResponse;

import java.time.LocalDateTime;

public interface PostsResponseEmp {
  int getId();

  LocalDateTime getTime();

  int getUserId();

  String getUserName();

  String getTitle();

  String getText();

  int getLikes();

  int getDislikes();

  int getComments();

  int getViews();
}
