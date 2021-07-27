package main.service;

import com.github.cage.Cage;
import com.github.cage.GCage;
import java.time.LocalDateTime;
import java.util.Base64;
import main.api.response.CaptchaResponse;
import main.models.CaptchaCode;
import main.repository.CaptchaCodesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CaptchaService {

  private final CaptchaCodesRepository captchaCodesRepository;

  public CaptchaService(CaptchaCodesRepository captchaCodesRepository) {
    this.captchaCodesRepository = captchaCodesRepository;
  }

  public CaptchaResponse createCaptcha() {
    Cage cage = new GCage();
    String textPage = cage.getTokenGenerator().next();
    byte[] fileContent = cage.draw(textPage);
    String encodedString = Base64.getEncoder().encodeToString(fileContent);
    String image = "data:image/png;base64, " + encodedString;

    StringBuilder secret = new StringBuilder();
    for (int i = 1; i <= 20; i++) {
      secret.append(encodedString.charAt((int) (Math.random() * encodedString.length())));
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
