package main.controllers;

import java.security.Principal;
import main.api.request.CommentRequest;
import main.api.request.GlobalSettingsRequest;
import main.api.request.ModerationRequest;
import main.api.request.UserUpdateRequest;
import main.api.response.CommentResponse;
import main.api.response.GlobalSettingsResponse;
import main.api.response.InitResponse;
import main.api.response.PostResponse;
import main.api.response.StatisticResponse;
import main.api.response.Tag2PostsResponse;
import main.api.response.UserResponse;
import main.api.response.calendarResponse.CalendarResponse;
import main.service.CalendarService;
import main.service.GlobalSettingsService;
import main.service.PostCommentsService;
import main.service.PostsService;
import main.service.Tag2PostsService;
import main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
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
  public GlobalSettingsResponse getGlobalSettings() {
    return globalSettingsService.getGlobalSettings();
  }

  @GetMapping("/calendar")
  public CalendarResponse getPostsByDate(
      @RequestParam(name = "year", required = false) String year) {
    return calendarService.getPostsByDate(year);
  }

  @PutMapping("/settings")
  public ResponseEntity<?> updateGlobalSettings(
      GlobalSettingsRequest request, Principal principal) {
    return globalSettingsService.updateSettings(request, principal);
  }

  @GetMapping("/tag")
  public Tag2PostsResponse getPostsByTags(
      @RequestParam(name = "query", required = false) String query) {
    return tag2PostsService.getGroupByTags(query);
  }

  @PostMapping(
      value = "/profile/my",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @ResponseStatus(value = HttpStatus.OK)
  public UserResponse editProfile(
      @ModelAttribute UserUpdateRequest request,
      @RequestPart(required = false) MultipartFile photo,
      Principal principal) {
    return userService.updateUser(request, photo, principal);
  }

  @PostMapping(value = "/profile/my")
  @ResponseStatus(value = HttpStatus.OK)
  public UserResponse editProfileDeletePhoto(
      @RequestBody UserUpdateRequest request,
      @RequestPart(required = false) MultipartFile photo,
      Principal principal) {
    return userService.updateUser(request, photo, principal);
  }

  @PostMapping("/moderation")
  @ResponseStatus(value = HttpStatus.OK)
  public PostResponse updateModStatus(@RequestBody ModerationRequest request, Principal principal) {
    return postsService.updateModStatus(request, principal);
  }

  @GetMapping("/statistics/my")
  public StatisticResponse getMyStatistic(Principal principal) {
    return postsService.getMyStatistic(principal);
  }

  @GetMapping("/statistics/all")
  public StatisticResponse getAllStatistic(Principal principal) {
    return postsService.getAllStatistic(principal);
  }

  @PostMapping("/comment")
  public ResponseEntity<?> createComment(@RequestBody CommentRequest request, Principal principal) {
    return postCommentsService.createComment(request, principal);
  }

  @PostMapping(
      value = "/image",
      consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<?> addPhoto(@RequestPart MultipartFile image, Principal principal) {
    return userService.addPhoto(image, principal);
  }
}
