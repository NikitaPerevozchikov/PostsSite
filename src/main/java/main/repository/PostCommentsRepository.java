package main.repository;

import java.util.List;
import main.api.response.postResponse.CommentEmp;
import main.models.PostComment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComment, Integer> {
  @Query (value = "select post_comments.id as id, "
      + "post_comments.time as time, "
      + "post_comments.text as text, "
      + "users.id as userId, "
      + "users.name as userName, "
      + "users.photo as userPhoto "
      + "from post_comments "
      + "join users on users.id = post_comments.user_id "
      + "where post_comments.post_id = :id "
      + ";",
      nativeQuery = true)
  public List<CommentEmp> getCommentsByPostId(@Param("id") int id);
}
