package main.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "captcha_codes")
@Getter
@Setter
public class CaptchaCode {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, columnDefinition = "DATETIME")
  private LocalDateTime time;

  @Column(nullable = false, columnDefinition = "TINYTEXT")
  private String code;

  @Column(name = "secret_code", nullable = false, columnDefinition = "TINYTEXT")
  private String secretCode;
}
