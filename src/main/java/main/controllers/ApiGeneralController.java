package main.controllers;

import java.util.Map;
import main.api.response.InitResponse;
import main.api.response.Tag2PostsResponse;
import main.service.GlobalSettingsService;
import main.service.Tag2PostsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

  private final InitResponse initResponse;
  private GlobalSettingsService globalSettingsService;
  private Tag2PostsService tag2PostsService;

  public ApiGeneralController(
      InitResponse initResponse,
      GlobalSettingsService globalSettingsService,
      Tag2PostsService tag2PostsService) {
    this.initResponse = initResponse;
    this.globalSettingsService = globalSettingsService;
    this.tag2PostsService = tag2PostsService;
  }

  @GetMapping("/init")
  public InitResponse init() {
    return initResponse;
  }

  @GetMapping("/settings")
  public Map<String, Boolean> getGlobalSettings() {
    return globalSettingsService.getGlobalSettings();
  }

  @GetMapping("/tag")
  public Tag2PostsResponse getGroupByTags(
      @RequestParam(name = "query", required = false) String query) {
    return tag2PostsService.getGroupByTags(query);
  }
}
