package main.controllers;

import java.security.Principal;
import main.api.response.PostsResponse;
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
  public PostsResponse getPostsByQuery(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("query") String query) {
    return postsService.getPostsByQuery(offset, limit, query);
  }

  @GetMapping("/byDate")
  public PostsResponse getGroupByDate(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("date") String date) {
    return postsService.getGroupByDate(offset, limit, date);
  }

  @GetMapping("/byTag")
  public PostsResponse getGroupByTag(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("tag") String tag) {
    return postsService.getGroupByTag(offset, limit, tag);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getPost(@PathVariable int id) {
    return postsService.getPost(id);
  }

  @GetMapping("/my")
  public PostsResponse getMyPosts(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("status") String status,
      Principal principal) {
    return postsService.getMyPosts(offset, limit, status, principal);
  }
}
