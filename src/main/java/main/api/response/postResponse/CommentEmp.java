package main.api.response.postResponse;

import java.time.LocalDateTime;
import java.util.Date;

public interface CommentEmp {
  Integer getId();
  LocalDateTime getTime();
  String getText();
  int getUserId();
  String getUserName();
  String getUSerPhoto();

}
