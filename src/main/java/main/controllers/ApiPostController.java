package main.controllers;

import java.security.Principal;
import main.api.request.PostRequest;
import main.api.request.VotesRequest;
import main.api.response.PostResponse;
import main.service.PostCommentsService;
import main.service.PostsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/post")
public class ApiPostController {

  private PostsService postsService;

  public ApiPostController(PostsService postsService, PostCommentsService postCommentsService) {

    this.postsService = postsService;
  }

  @GetMapping("")
  public ResponseEntity<?> getGroupPosts(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("mode") String mode) {
    return postsService.getGroupPosts(offset, limit, mode);
  }

  @GetMapping("/search")
  public ResponseEntity<?> getPostsByQuery(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("query") String query) {
    return postsService.getPostsByQuery(offset, limit, query);
  }

  @GetMapping("/byDate")
  public ResponseEntity<?> getGroupByDate(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("date") String date) {
    return postsService.getGroupByDate(offset, limit, date);
  }

  @GetMapping("/byTag")
  public ResponseEntity<?> getGroupByTag(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("tag") String tag) {
    return postsService.getGroupByTag(offset, limit, tag);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getPost(@PathVariable int id) {
    return postsService.getPost(id);
  }

  @GetMapping("/moderation")
  public ResponseEntity<?> getMyModPosts(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("status") String status,
      Principal principal) {
    return postsService.getMyModPosts(offset, limit, status, principal);
  }

  @GetMapping("/my")
  public ResponseEntity<?> getMyPosts(
      @RequestParam("offset") int offset,
      @RequestParam("limit") int limit,
      @RequestParam("status") String status,
      Principal principal) {
    return postsService.getMyPosts(offset, limit, status, principal);
  }

  @PostMapping("")
  public ResponseEntity<?> addPost(@RequestBody PostRequest request, Principal principal) {
    return postsService.addPost(request, principal);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updatePost(
      @PathVariable int id, @RequestBody PostRequest request, Principal principal) {
    return postsService.updatePost(id, request, principal);
  }

  @PostMapping("/like")
  public ResponseEntity<?> addLike(@RequestBody VotesRequest request, Principal principal) {
    return postsService.addVotes(request, principal, "like");
  }

  @PostMapping("/dislike")
  public ResponseEntity<?> addDisLike(@RequestBody VotesRequest request, Principal principal) {
    return postsService.addVotes(request, principal, "dislike");
  }
}
