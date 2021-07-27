package main.service;

import java.util.HashMap;
import java.util.Map;
import main.repository.GlobalSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingsService {

  private final GlobalSettingsRepository globalSettingsRepository;

  public GlobalSettingsService(GlobalSettingsRepository globalSettingsRepository) {
    this.globalSettingsRepository = globalSettingsRepository;
  }

  public Map<String, Boolean> getGlobalSettings() {
    Map<String, Boolean> settings = new HashMap<>();
    globalSettingsRepository
        .findAll()
        .forEach(e -> settings.put(e.getCode(), e.getValue().equals("YES")));
    return settings;
  }
}
