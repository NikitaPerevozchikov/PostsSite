package main.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import main.exceptions.ExceptionBadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

  private final Cloudinary cloudinary;

  @Autowired
  public ImageService(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  public String uploadImage(MultipartFile file) {
    Map options = new HashMap<>();
    options.put("folder", "upload" + generatorPatch());
    return getUrlFile(options, file);
  }

  public String uploadAvatar(MultipartFile file, int userId) {
    Map options = new HashMap<>();
    options.put("folder", "avatars");
    options.put("public_id", "avatar_user_" + userId);
    options.put("transformation", new Transformation().width(36).height(36));
    return getUrlFile(options, file);
  }

  public void deleteAvatar(String path) {
    String[] fileName = Objects.requireNonNull(path.split("/"));
    String publicId =
        fileName[fileName.length - 2]
            + "/"
            + fileName[fileName.length - 1].substring(
                0, fileName[fileName.length - 1].lastIndexOf('.'));
    try {
      cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    } catch (IOException e) {
      throw new ExceptionBadRequest();
    }
  }

  private String getUrlFile(Map options, MultipartFile image) {
    File convertFile = new File(Objects.requireNonNull(image.getOriginalFilename()));
    try (FileOutputStream fileOutputStream = new FileOutputStream(convertFile)) {
      fileOutputStream.write(image.getBytes());
      Map uploadResult = cloudinary.uploader().upload(convertFile, options);
      return uploadResult.get("url").toString();
    } catch (IOException e) {
      throw new ExceptionBadRequest();
    }
  }

  private String generatorPatch() {
    String abc = "abcdefghijklmnopqrstuvwxyz0123456789";
    StringBuilder patch = new StringBuilder();
    patch.append("/");
    for (int i = 1; i <= 3; i++) {
      patch.append(abc.charAt(new Random().nextInt(abc.length())));
      patch.append(abc.charAt(new Random().nextInt(abc.length())));
      patch.append("/");
    }
    return patch.toString();
  }
}
