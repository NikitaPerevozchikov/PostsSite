package main.api.response.postResponse;

import java.time.LocalDateTime;

public interface PostResponseEmp {

  Integer getId();

  LocalDateTime getTime();

  int getUserId();

  String getUserName();

  String getTitle();

  String getText();

  int getLikes();

  int getDislikes();

  int getViews();

  String[] getTags();

}
