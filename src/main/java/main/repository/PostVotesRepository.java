package main.repository;

import main.models.PostVote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVote, Integer> {

  @Query(
      value =
          "insert into post_votes set user_id= :user_id, post_id= :post_id , time = time(now()), value= :value ; ",
      nativeQuery = true)
  @Modifying
  void makeLike(
      @Param("user_id") int userId, @Param("post_id") int postId, @Param("value") int value);

  @Query(
      value = "select * from post_votes where user_id = :user_id and post_id = :post_id ;",
      nativeQuery = true)
  PostVote findByUserIdAndPostId(@Param("user_id") int userId, @Param("post_id") int postId);
}
