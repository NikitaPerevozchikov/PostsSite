package main.repository;

import main.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<User, Integer> {
  @Query(value = " select * from users where email = :email ;", nativeQuery = true)
  User findByEmail(@Param("email") String email);

  @Query(
      value = " select * from users where email = :email and password = :password ;",
      nativeQuery = true)
  User checkUserExist(@Param("email") String email, @Param("password") String password);
}
