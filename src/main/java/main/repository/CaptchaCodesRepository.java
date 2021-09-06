package main.repository;

import main.models.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CaptchaCodesRepository extends CrudRepository<CaptchaCode, Integer> {

  @Modifying
  @Query(
      value = "delete from captcha_codes where time <= date_sub(now(), interval 2 hour);",
      nativeQuery = true)
  void deleteLessTwoHours();

  @Query(value = "select * from captcha_codes where secret_code= :code ;", nativeQuery = true)
  CaptchaCode findBySelectCode(@Param("code") String code);
}
