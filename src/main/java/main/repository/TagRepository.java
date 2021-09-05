package main.repository;

import main.models.Tag;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

  @Query(value = "select * from tags where name = :name ;", nativeQuery = true)
  Tag findByName(@Param("name") String name);

  @Query(value = "insert into tags set name = :name ;", nativeQuery = true)
  @Modifying
  void createNewTag(@Param("name") String name);
}
