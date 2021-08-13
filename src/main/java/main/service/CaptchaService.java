package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;
import main.api.response.CaptchaResponse;
import main.models.CaptchaCode;
import main.repository.CaptchaCodesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CaptchaService {

  private final CaptchaCodesRepository captchaCodesRepository;

  @Autowired
  public CaptchaService(CaptchaCodesRepository captchaCodesRepository) {
    this.captchaCodesRepository = captchaCodesRepository;
  }

  public CaptchaResponse addCaptcha() {
    Cage cage = new GCage();
    String textPage = cage.getTokenGenerator().next();
    byte[] fileContent = cage.draw(textPage);
    String encodedString = Base64.getEncoder().encodeToString(fileContent);
    String image = "data:image/png;base64, " + encodedString;
    StringBuilder secret = new StringBuilder();
    for (int i = 1; i <= 45; i++) {
      secret.append(encodedString.charAt(new Random().nextInt(encodedString.length())));
    }
    CaptchaCode captchaCode = new CaptchaCode();
    captchaCode.setTime(LocalDateTime.now());
    captchaCode.setCode(textPage);
    captchaCode.setSecretCode(secret.toString());
    captchaCodesRepository.deleteLessTwoHours();
    captchaCodesRepository.save(captchaCode);
    return new CaptchaResponse(secret.toString(), image);
  }
}
