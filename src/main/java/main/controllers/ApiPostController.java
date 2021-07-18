package main.controllers;

import java.util.List;
import main.api.response.calendarResponse.CalendarResponse;
import main.api.response.postResponse.PostResponse;
import main.api.response.postResponse.PostResponseEmp;
import main.api.response.postsResponse.PostsResponse;
import main.models.Post;
import main.service.PostsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

  private PostsService postsService;

  public ApiPostController(PostsService postsService) {

    this.postsService = postsService;
  }

  @GetMapping()
  public PostsResponse getGroupPosts(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("mode") String mode) {
    return postsService.getGroupPosts(offset, limit, mode);
  }

  @GetMapping("/search")
  public PostsResponse search(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("query") String query) {
    return postsService.search(offset, limit, query);
  }
  @GetMapping("/byDate")
  public PostsResponse groupByDate(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("date") String date) {
    return postsService.getGroupDate(offset, limit, date);
  }

  @GetMapping("/byTag")
  public PostsResponse groupByTag(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("tag") String tag) {
    return postsService.getGroupTag(offset, limit, tag);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getPost(@PathVariable int id) {
    return postsService.post(id);
  }

}
