package main.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.TimeZone;
import main.api.request.PasswordRequest;
import main.api.request.UserRequest;
import main.api.request.UserUpdateRequest;
import main.api.response.UserResponse;
import main.exceptions.ExceptionBadRequest;
import main.exceptions.ExceptionNotFound;
import main.exceptions.ExceptionUnauthorized;
import main.models.CaptchaCode;
import main.models.User;
import main.repository.CaptchaCodesRepository;
import main.repository.GlobalSettingsRepository;
import main.repository.UsersRepository;
import main.security.SecurityUser;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  private final String abc = "abcdefghijklmnopqrstuvwxyz0123456789";

  @Autowired
  public UserService(
      UsersRepository usersRepository,
      CaptchaCodesRepository captchaCodesRepository,
      PasswordEncoder passwordEncoder,
      GlobalSettingsRepository globalSettingsRepository) {
    this.usersRepository = usersRepository;
    this.captchaCodesRepository = captchaCodesRepository;
    this.passwordEncoder = passwordEncoder;
    this.globalSettingsRepository = globalSettingsRepository;
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
        user.setPhoto("");
      }
      if (request.getRemovePhoto() == 0) {
        String pathLoad = "src/main/resources/avatars/";
        if (loadingFile(photo, pathLoad, true)) {
          throw new ExceptionBadRequest();
        }
        user.setPhoto(pathLoad + photo.getOriginalFilename());
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
    User user = changeAuthorization(principal);
    UserResponse response = new UserResponse();
    String pathLoad = "src/main/resources/upload/";
    if (loadingFile(image, pathLoad + generatorPatch(), false)) {
      response.getErrors().put("image", "Размер файла превышает допустимый размер");
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    usersRepository.save(user);
    return new ResponseEntity<>(pathLoad + image.getOriginalFilename(), HttpStatus.OK);
  }

  public UserResponse recoveryPassword(UserRequest request) {
    UserResponse response = new UserResponse();
    User user = usersRepository.findByEmail(request.getEmail());
    if (user != null) {
      user.setCode(generatorCode());
      usersRepository.save(user);
      response.setResult(true);
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

  private boolean loadingFile(MultipartFile image, String pathLoad, boolean isCompress) {
    String[] fileName = Objects.requireNonNull(image.getOriginalFilename()).split("\\.");
    if (!(fileName[fileName.length - 1].equals("jpg")
            || fileName[fileName.length - 1].equals("png"))
        || image.getSize() > 5 * 1048576) {
      return true;
    }
    String path = pathLoad + image.getOriginalFilename();
    try {
      Files.createDirectories(Paths.get(pathLoad));
      File photo = new File(path);
      FileOutputStream fileOutputStream = new FileOutputStream(photo);
      fileOutputStream.write(image.getBytes());
      fileOutputStream.close();
      if (isCompress) {
        Thumbnails.of(path).size(36, 36).toFile(path);
      }
    } catch (IOException e) {
      return true;
    }
    return false;
  }

  private String generatorCode() {
    StringBuilder code = new StringBuilder();
    for (int i = 1; i <= 45; i++) {
      code.append(abc.charAt(new Random().nextInt(abc.length())));
    }
    return code.toString();
  }

  private String generatorPatch() {
    StringBuilder patch = new StringBuilder();
    for (int i = 1; i <= 3; i++) {
      patch.append(abc.charAt(new Random().nextInt(abc.length())));
      patch.append(abc.charAt(new Random().nextInt(abc.length())));
      patch.append("/");
    }
    return patch.toString();
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
