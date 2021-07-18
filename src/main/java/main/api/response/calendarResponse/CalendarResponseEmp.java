package main.api.response.calendarResponse;

import java.time.LocalDateTime;
import java.util.Date;

public interface CalendarResponseEmp {
  String getYear();

  Date getDate();

  int getCount();
}
