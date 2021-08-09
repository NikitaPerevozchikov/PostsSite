package main.service;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import main.api.response.calendarResponse.CalendarResponse;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {
  private final PostsRepository postsRepository;
  private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

  @Autowired
  public CalendarService(PostsRepository postsRepository) {
    this.postsRepository = postsRepository;
  }

  public ResponseEntity<?> getPostsByDate(String year) {
    Set<String> years = new HashSet<>();
    Map<String, Integer> posts = new HashMap<>();
    CalendarResponse response = new CalendarResponse();
    postsRepository
        .findPostsByDate()
        .forEach(
            e -> {
              years.add(e.getYear());
              if (e.getYear().equals(year == null ? Year.now().toString() : year)) {
                posts.put(format.format(e.getDate()), e.getCount());
              }
            });
    response.setYears(years);
    response.setPosts(posts);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
