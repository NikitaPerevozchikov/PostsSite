package main.controllers;

import main.api.response.PostsResponse;
import main.service.PostsService;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
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
      @RequestParam("offset") int offset, @RequestParam("limit") int limit,
      @RequestParam("mode") String mode) {
    return postsService.getGroupPosts(offset, limit, mode);
  }
  
}
