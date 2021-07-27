package main.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;
import main.api.request.UserRequest;
import main.api.response.RegisterResponse;
import main.models.CaptchaCode;
import main.models.User;
import main.repository.CaptchaCodesRepository;
import main.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UsersRepository usersRepository;
  private final CaptchaCodesRepository captchaCodesRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(
      UsersRepository usersRepository,
      CaptchaCodesRepository captchaCodesRepository,
      PasswordEncoder passwordEncoder) {
    this.usersRepository = usersRepository;
    this.captchaCodesRepository = captchaCodesRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public RegisterResponse createUser(UserRequest userRequest) {
    RegisterResponse response = new RegisterResponse();

    User user = new User();
    if (usersRepository.findByEmail(userRequest.getEmail()) == null) {
      user.setEmail(userRequest.getEmail());
    } else {
      response.getErrors().put("email", "Этот e-mail уже зарегистрирован");
    }

    if (!userRequest.getName().trim().isEmpty()) {
      user.setName(userRequest.getName());
    } else {
      response.getErrors().put("name", "Имя указано неверно");
    }

    if (userRequest.getPassword().length() >= 6) {
      user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    } else {
      response.getErrors().put("password", "Пароль короче 6-ти символов");
    }
    CaptchaCode captchaCode =
        captchaCodesRepository.findBySelectCode(userRequest.getCaptchaSecret());

    if (captchaCode != null && captchaCode.getCode().equals(userRequest.getCaptcha())) {
      user.setCode(userRequest.getCaptcha());
    } else {
      response.getErrors().put("captcha", "Код с картинки введён неверно");
    }

    if (response.getErrors().isEmpty()) {
      response.setResult(true);
      user.setRegTime(
          LocalDateTime.ofInstant(
              Instant.ofEpochMilli(System.currentTimeMillis()), TimeZone.getDefault().toZoneId()));
      usersRepository.save(user);
    }
    return response;
  }
}
