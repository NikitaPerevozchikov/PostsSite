package main.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.security.Principal;
import main.api.request.CommentRequest;
import main.api.request.GlobalSettingsRequest;
import main.api.request.ModerationRequest;
import main.api.response.InitResponse;
import main.api.response.StatisticResponse;
import main.service.CalendarService;
import main.service.GlobalSettingsService;
import main.service.PostCommentsService;
import main.service.PostsService;
import main.service.Tag2PostsService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

  private final InitResponse initResponse;
  private final GlobalSettingsService globalSettingsService;
  private final Tag2PostsService tag2PostsService;
  private final UserService userService;
  private final PostsService postsService;
  private final PostCommentsService postCommentsService;
  private final CalendarService calendarService;

  @Autowired
  public ApiGeneralController(
      InitResponse initResponse,
      GlobalSettingsService globalSettingsService,
      Tag2PostsService tag2PostsService,
      UserService userService,
      PostsService postsService,
      PostCommentsService postCommentsService,
      CalendarService calendarService) {
    this.initResponse = initResponse;
    this.globalSettingsService = globalSettingsService;
    this.tag2PostsService = tag2PostsService;
    this.userService = userService;
    this.postsService = postsService;
    this.postCommentsService = postCommentsService;
    this.calendarService = calendarService;
  }

  @GetMapping("/init")
  public InitResponse init() {
    return initResponse;
  }

  @GetMapping("/settings")
  public ResponseEntity<?> getGlobalSettings() {
    return globalSettingsService.getGlobalSettings();
  }

  @GetMapping("/calendar")
  public ResponseEntity<?> getPostsByDate(
      @RequestParam(name = "year", required = false) String year) {
    return calendarService.getPostsByDate(year);
  }

  @PostMapping("/settings")
  public ResponseEntity<?> updateGlobalSettings(
      GlobalSettingsRequest request, Principal principal) {
    return globalSettingsService.updateSettings(request, principal);
  }

  @GetMapping("/tag")
  public ResponseEntity<?> getPostsByTags(
      @RequestParam(name = "query", required = false) String query) {
    return tag2PostsService.getGroupByTags(query);
  }

  @PostMapping("/profile/my")
  public ResponseEntity<?> updateUser(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) @JsonProperty("e_mail") String email,
      @RequestParam(required = false) String password,
      @RequestParam(required = false) MultipartFile photo,
      @RequestParam(required = false) Integer removePhoto,
      Principal principal) {
    return userService.updateUser(name, email, password, photo, removePhoto, principal);
  }

  @PostMapping("/moderation")
  public ResponseEntity<?> updateModStatus(
      @RequestBody ModerationRequest request, Principal principal) {
    return postsService.updateModStatus(request, principal);
  }

  @GetMapping("/statistics/my")
  public ResponseEntity<?> getMyStatistic(Principal principal) {
    return postsService.getMyStatistic(principal);
  }

  @GetMapping("/statistics/all")
  public ResponseEntity<StatisticResponse> getAllStatistic(Principal principal) {
    return postsService.getAllStatistic(principal);
  }

  @PostMapping("/comment")
  public ResponseEntity<?> createComment(@RequestBody CommentRequest request, Principal principal) {
    return postCommentsService.createComment(request, principal);
  }

  @PostMapping("/image")
  public ResponseEntity<?> addPhoto(@RequestPart MultipartFile image, Principal principal) {
    return userService.addPhoto(image, principal);
  }
}
