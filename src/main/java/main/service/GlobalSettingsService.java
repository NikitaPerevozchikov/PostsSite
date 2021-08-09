package main.service;

import java.security.Principal;
import main.api.request.GlobalSettingsRequest;
import main.api.response.GlobalSettingsResponse;
import main.models.User;
import main.repository.GlobalSettingsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GlobalSettingsService {

  private final GlobalSettingsRepository globalSettingsRepository;
  private final UsersRepository usersRepository;

  @Autowired
  public GlobalSettingsService(
      GlobalSettingsRepository globalSettingsRepository, UsersRepository usersRepository) {
    this.globalSettingsRepository = globalSettingsRepository;
    this.usersRepository = usersRepository;
  }

  public ResponseEntity<?> getGlobalSettings() {

    GlobalSettingsResponse response = new GlobalSettingsResponse();
    response.setMultiuserMode(
        globalSettingsRepository.findValueByCode("MULTIUSER_MODE").equals("YES"));
    response.setPostPremoderation(
        globalSettingsRepository.findValueByCode("POST_PREMODERATION").equals("YES"));
    response.setStatisticsIsPublic(
        globalSettingsRepository.findValueByCode("STATISTICS_IS_PUBLIC").equals("YES"));
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  public ResponseEntity<?> updateSettings(GlobalSettingsRequest request, Principal principal) {
    User user;
    try {
      user = usersRepository.findByEmailAndMod(principal.getName());
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    if (user == null) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

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
