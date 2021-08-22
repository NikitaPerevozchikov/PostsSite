package main.config;

import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.port}")
  private Integer port;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @Value("${spring.mail.properties.mail.transport.protocol}")
  private String mailTransportProtocol;

  @Value("${spring.mail.properties.mail.smtp.auth}")
  private String mailSmtpAuth;

  @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
  private String mailStarttlsEnable;

  @Value("${spring.mail.properties.mail.debug}")
  private String mailDebug;

  @Bean
  public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost(host);
    mailSender.setPort(port);
    mailSender.setUsername(username);
    mailSender.setPassword(password);
    mailSender.setJavaMailProperties(getMailProperties());
    return mailSender;
  }

  private Properties getMailProperties() {
    Properties properties = new Properties();
    properties.setProperty("mail.transport.protocol", mailTransportProtocol);
    properties.setProperty("mail.smtp.auth", mailSmtpAuth);
    properties.setProperty("mail.smtp.starttls.enable", mailStarttlsEnable);
    properties.setProperty("mail.debug", mailDebug);
    return properties;
  }
}
