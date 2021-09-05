package main.service;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import main.api.request.EmailRequest;
import main.api.request.PasswordRequest;
import main.api.request.UserRequest;
import main.api.request.UserUpdateRequest;
import main.api.response.UserResponse;
import main.exceptions.ExceptionNotFound;
import main.exceptions.ExceptionUnauthorized;
import main.models.CaptchaCode;
import main.models.User;
import main.repository.CaptchaCodesRepository;
import main.repository.GlobalSettingsRepository;
import main.repository.UsersRepository;
import main.security.SecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

  private final UsersRepository usersRepository;
  private final CaptchaCodesRepository captchaCodesRepository;
  private final PasswordEncoder passwordEncoder;
  private final GlobalSettingsRepository globalSettingsRepository;
  private final ImageService cloudinaryService;
  private final JavaMailSender javaMailSender;

  @Autowired
  public UserService(
      UsersRepository usersRepository,
      CaptchaCodesRepository captchaCodesRepository,
      PasswordEncoder passwordEncoder,
      GlobalSettingsRepository globalSettingsRepository,
      ImageService cloudinaryService,
      @Qualifier("javaMailSender") JavaMailSender javaMailSender) {
    this.usersRepository = usersRepository;
    this.captchaCodesRepository = captchaCodesRepository;
    this.passwordEncoder = passwordEncoder;
    this.globalSettingsRepository = globalSettingsRepository;
    this.cloudinaryService = cloudinaryService;
    this.javaMailSender = javaMailSender;
  }

  public UserResponse addUser(UserRequest request) {
    if (globalSettingsRepository.findValueByCode("MULTIUSER_MODE").equals("NO")) {
      throw new ExceptionNotFound();
    }
    User user = new User();
    UserResponse response = new UserResponse();

    if (changeParameterEmail(request.getEmail(), response)) {
      user.setEmail(request.getEmail());
    }

    if (changeParameterName(request.getName(), response)) {
      user.setName(request.getName());
    }

    if (changeParameterPassword(request.getPassword(), response)) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    if (changeParameterCaptcha(request.getCaptcha(), request.getCaptchaSecret(), response)) {
      user.setCode(request.getCaptcha());
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

  public UserResponse updateUser(
      UserUpdateRequest request, MultipartFile photo, Principal principal) {
    User user = changeAuthorization(principal);
    UserResponse response = new UserResponse();

    if (request.getRemovePhoto() != null) {
      if (request.getRemovePhoto() == 1) {
        cloudinaryService.deleteAvatar(user.getPhoto());
        user.setPhoto("");
      }
      if (request.getRemovePhoto() == 0) {
        if (changeFile(photo)) {
          response.getErrors().put("image", "Размер файла превышает допустимый размер");
        } else {
          String path = cloudinaryService.uploadAvatar(photo, user.getId());
          user.setPhoto(path);
        }
      }
    }

    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
      if (changeParameterEmail(request.getEmail(), response)) {
        user.setEmail(request.getEmail());
      }
    }

    if (request.getName() != null) {
      if (changeParameterName(request.getName(), response)) {
        user.setName(request.getName());
      }
    }

    if (request.getPassword() != null) {
      if (changeParameterPassword(request.getPassword(), response)) {
        user.setPassword(passwordEncoder.encode(request.getPassword()));
      }
    }

    if (response.getErrors().isEmpty()) {
      response.setResult(true);
      usersRepository.save(user);

      UserDetails userDetails = SecurityUser.fromUser(user);
      Authentication auth =
          new UsernamePasswordAuthenticationToken(
              userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(auth);
    }
    return response;
  }

  public UserResponse updatePassword(PasswordRequest request) {
    UserResponse response = new UserResponse();
    changeParameterCode(request.getCaptchaSecret(), response);
    changeParameterPassword(request.getPassword(), response);
    changeParameterCaptcha(request.getCaptcha(), request.getCaptchaSecret(), response);
    return response;
  }

  public ResponseEntity<?> addPhoto(MultipartFile image, Principal principal) {
    changeAuthorization(principal);
    if (changeFile(image)) {
      UserResponse response = new UserResponse();
      response.getErrors().put("image", "Размер файла превышает допустимый размер");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    String path = cloudinaryService.uploadImage(image);
    return new ResponseEntity<>(path, HttpStatus.OK);
  }

  public UserResponse recoveryPassword(EmailRequest request) {
    UserResponse response = new UserResponse();
    User user = usersRepository.findByEmail(request.getEmail());
    if (user != null) {
      String codeRecovery = generatorCode();
      user.setCode(codeRecovery);
      usersRepository.save(user);
      response.setResult(true);

      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setTo(request.getEmail());
      mailMessage.setSubject("Ссылка для восстановления пароля");
      mailMessage.setText("/login/change-password/" + codeRecovery);
      javaMailSender.send(mailMessage);
    }

    return response;
  }

  private boolean changeParameterEmail(String email, UserResponse response) {
    if (usersRepository.findByEmail(email) == null) {
      return true;
    } else {
      response.getErrors().put("email", "Этот e-mail уже зарегистрирован");
      return false;
    }
  }

  private boolean changeParameterName(String name, UserResponse response) {
    if (!name.trim().isEmpty()) {
      return true;
    } else {
      response.getErrors().put("name", "Имя указано неверно");
      return false;
    }
  }

  private boolean changeParameterPassword(String password, UserResponse response) {
    if (password.length() >= 6) {
      return true;
    } else {
      response.getErrors().put("password", "Пароль короче 6-ти символов");
      return false;
    }
  }

  private boolean changeParameterCaptcha(
      String captcha, String captchaSecret, UserResponse response) {
    CaptchaCode captchaCode = captchaCodesRepository.findBySelectCode(captchaSecret);
    if (captchaCode != null && captchaCode.getCode().equals(captcha)) {
      return true;
    } else {
      response.getErrors().put("captcha", "Код с картинки введён неверно");
      return false;
    }
  }

  private boolean changeParameterCode(String captchaSecret, UserResponse response) {
    CaptchaCode captchaCode = captchaCodesRepository.findBySelectCode(captchaSecret);
    if (captchaCode != null) {
      return true;
    } else {
      response
          .getErrors()
          .put(
              "code",
              "Ссылка для восстановления пароля устарела. <a href=\\\"/auth/restore\\\">Запросить ссылку снова</a>");
      return false;
    }
  }

  private boolean changeFile(MultipartFile image) {
    String[] fileName = Objects.requireNonNull(image.getOriginalFilename()).split("\\.");
    return !(fileName[fileName.length - 1].equals("jpg")
            || fileName[fileName.length - 1].equals("png"))
        || image.getSize() > 5 * 1048576;
  }

  private String generatorCode() {
    StringBuilder code = new StringBuilder();
    for (int i = 1; i <= 45; i++) {
      String abc = "abcdefghijklmnopqrstuvwxyz0123456789";
      code.append(abc.charAt(new Random().nextInt(abc.length())));
    }
    return code.toString();
  }

  private User changeAuthorization(Principal principal) {
    User user;
    try {
      user = usersRepository.findByEmail(principal.getName());
    } catch (Exception e) {
      throw new ExceptionUnauthorized();
    }
    if (user == null) {
      throw new ExceptionUnauthorized();
    }
    return user;
  }
}
