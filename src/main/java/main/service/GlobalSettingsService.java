package main.service;

import java.security.Principal;
import main.api.request.GlobalSettingsRequest;
import main.api.response.GlobalSettingsResponse;
import main.repository.GlobalSettingsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingsService {

  private final GlobalSettingsRepository globalSettingsRepository;

  @Autowired
  public GlobalSettingsService(
      GlobalSettingsRepository globalSettingsRepository, UsersRepository usersRepository) {
    this.globalSettingsRepository = globalSettingsRepository;
  }

  public GlobalSettingsResponse getGlobalSettings() {
    GlobalSettingsResponse response = new GlobalSettingsResponse();
    response.setMultiuserMode(
        globalSettingsRepository.findValueByCode("MULTIUSER_MODE").equals("YES"));
    response.setPostPremoderation(
        globalSettingsRepository.findValueByCode("POST_PREMODERATION").equals("YES"));
    response.setStatisticsIsPublic(
        globalSettingsRepository.findValueByCode("STATISTICS_IS_PUBLIC").equals("YES"));
    return response;
  }

  public ResponseEntity<?> updateSettings(GlobalSettingsRequest request, Principal principal) {
    if (request.getMultiuserMode() != null) {
      globalSettingsRepository.updateSettingByCode(
          "MULTIUSER_MODE", (request.getMultiuserMode() ? "YES" : "NO"));
    }
    if (request.getPostPremoderation() != null) {
      globalSettingsRepository.updateSettingByCode(
          "POST_PREMODERATION", (request.getPostPremoderation() ? "YES" : "NO"));
    }
    if (request.getStatisticsIsPublic() != null) {
      globalSettingsRepository.updateSettingByCode(
          "STATISTICS_IS_PUBLIC", (request.getStatisticsIsPublic() ? "YES" : "NO"));
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
