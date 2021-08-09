package main.repository;

import main.models.GlobalSetting;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSetting, Integer> {

  @Query(value = "select value from global_settings where code = :code ;", nativeQuery = true)
  String findValueByCode(@Param("code") String code);

  @Query(
      value = "update global_settings set value = :value where code = :code ;",
      nativeQuery = true)
  @Modifying
  void updateSettingByCode(@Param("code") String code, @Param("value") String value);
}
