package main.controllers;

import java.security.Principal;
import main.api.request.PostRequest;
import main.api.request.VotesRequest;
import main.api.response.GetPostResponse;
import main.api.response.PostResponse;
import main.api.response.PostsResponse;
import main.service.PostCommentsService;
import main.service.PostsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

  private final PostsService postsService;

  public ApiPostController(PostsService postsService, PostCommentsService postCommentsService) {

    this.postsService = postsService;
  }

  @GetMapping("")
  @ResponseStatus(value = HttpStatus.OK)
  public PostsResponse getGroupPosts(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "limit", defaultValue = "10") int limit,
      @RequestParam(name = "mode", defaultValue = "recent") String mode) {
    return postsService.getGroupPosts(offset, limit, mode);
  }

  @GetMapping("/search")
  @ResponseStatus(value = HttpStatus.OK)
  public PostsResponse getPostsByQuery(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "limit", defaultValue = "10") int limit,
      @RequestParam("query") String query) {
    return postsService.getPostsByQuery(offset, limit, query);
  }

  @GetMapping("/byDate")
  @ResponseStatus(value = HttpStatus.OK)
  public PostsResponse getGroupByDate(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "limit", defaultValue = "10") int limit,
      @RequestParam(name = "date") String date) {
    return postsService.getGroupByDate(offset, limit, date);
  }

  @GetMapping("/byTag")
  @ResponseStatus(value = HttpStatus.OK)
  public PostsResponse getGroupByTag(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "limit", defaultValue = "10") int limit,
      @RequestParam(name = "tag") String tag) {
    return postsService.getGroupByTag(offset, limit, tag);
  }

  @GetMapping("/{id}")
  public GetPostResponse getPost(@PathVariable int id) {
    return postsService.getPost(id);
  }

  @GetMapping("/moderation")
  @ResponseStatus(value = HttpStatus.OK)
  public PostsResponse getPostsForMod(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "limit", defaultValue = "10") int limit,
      @RequestParam(name = "status", defaultValue = "new") String status,
      Principal principal) {
    return postsService.getPostsForMod(offset, limit, status, principal);
  }

  @GetMapping("/my")
  @ResponseStatus(value = HttpStatus.OK)
  public PostsResponse getMyPosts(
      @RequestParam(name = "offset", defaultValue = "0") int offset,
      @RequestParam(name = "limit", defaultValue = "10") int limit,
      @RequestParam(name = "status", defaultValue = "published") String status,
      Principal principal) {
    return postsService.getMyPosts(offset, limit, status, principal);
  }

  @PostMapping("")
  @ResponseStatus(value = HttpStatus.OK)
  public PostResponse addPost(@RequestBody PostRequest request, Principal principal) {
    return postsService.addPost(request, principal);
  }

  @PutMapping("/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public PostResponse updatePost(
      @PathVariable int id, @RequestBody PostRequest request, Principal principal) {
    return postsService.updatePost(id, request, principal);
  }

  @PostMapping("/like")
  @ResponseStatus(value = HttpStatus.OK)
  public PostResponse addLike(@RequestBody VotesRequest request, Principal principal) {
    return postsService.addVotes(request, principal, "like");
  }

  @PostMapping("/dislike")
  @ResponseStatus(value = HttpStatus.OK)
  public PostResponse addDisLike(@RequestBody VotesRequest request, Principal principal) {
    return postsService.addVotes(request, principal, "dislike");
  }
}
