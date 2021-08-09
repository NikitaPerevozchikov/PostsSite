package main.repository;

import main.models.PostComment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComment, Integer> {

  @Query(value = "select * from post_comments where id = :id group by id;", nativeQuery = true)
  PostComment findByCommentId(@Param("id") int id);
}
