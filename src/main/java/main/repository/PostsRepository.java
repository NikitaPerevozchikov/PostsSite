package main.repository;

import main.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsRepository extends PagingAndSortingRepository<Post, Integer> {


  @Query(value = "select posts.id as id, "
      + "posts.is_active as is_active, "
      + "posts.moderation_status as moderation_status, "
      + "posts.moderator_id as moderator_id, "
      + "posts.user_id as user_id, "
      + "posts.time as time, "
      + "posts.title as title, "
      + "posts.text as text, "
      + "posts.view_count as view_count, "
      + "sum(case when post_votes.value = 1 then 1 else 0 end) as like_count, "
      + "sum(case when post_votes.value = -1 then 1 else 0 end) as dislike_count, "
      + "count_comments.comments as comment_count "
      + "from posts "
      + "join users on users.id = posts.user_id "
      + "join "
      + "(select posts.id, count(post_comments.post_id) as comments "
      + "from posts "
      + "left join post_comments on posts.id = post_comments.post_id "
      + "group by post_comments.post_id) as count_comments on posts.id = count_comments.id "
      + "left join post_votes on posts.id=post_votes.post_id "
      + "where posts.is_active = 1 "
      + "and posts.moderation_status = 'ACCEPTED' "
      + "and posts.time <= now() "
      + "group by posts.id",
      nativeQuery = true)
  Page<Post> findGroupById(Pageable pageable);


}
