package main.repository;

import java.util.List;
import main.api.response.calendarResponse.CalendarResponseEmp;
import main.api.response.postResponse.PostResponse;
import main.api.response.postResponse.PostResponseEmp;
import main.api.response.postsResponse.PostsResponseEmp;
import main.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsRepository extends CrudRepository<Post, Integer> {

  String QUERY =
      "select posts.id as id, "
          + "posts.time as time, "
          + "users.id as userId, "
          + "users.name as userName, "
          + "posts.title as title, "
          + "posts.text as text, "
          + "sum(case when post_votes.value = 1 then 1 else 0 end) as likes, "
          + "sum(case when post_votes.value = -1 then 1 else 0 end) as dislikes, "
          + "coalesce (count_comments.comments, 0) as comments, "
          + "posts.view_count as views "
          + "from posts "
          + "join users on users.id = posts.user_id "
          + "left join "
          + "(select posts.id, count(post_comments.post_id) as comments "
          + "from posts "
          + "left join post_comments on posts.id = post_comments.post_id "
          + "group by post_comments.post_id) as count_comments on posts.id = count_comments.id "
          + "left join post_votes on posts.id=post_votes.post_id ";

  @Query(value = QUERY
      + "group by posts.id",
      nativeQuery = true)
  Page<PostsResponseEmp> findGroupById(Pageable pageable);

  @Query(
      value =
          QUERY
              + "where posts.is_active = 1 "
              + "and posts.moderation_status = 'ACCEPTED' "
              + "and posts.time <= now() "
              + "and title like concat('%',:query,'%') or "
              + "text like concat('%',:query,'%') "
              + "group by posts.id",
      nativeQuery = true)
  Page<PostsResponseEmp> search(@Param("query") String query, Pageable pageable);

  @Query(
      value =
          QUERY
              + "where posts.is_active = 1 "
              + "and posts.moderation_status = 'ACCEPTED' "
              + "and posts.time <= now() "
              + "and date(posts.time) like :date "
              + "group by posts.id",
      nativeQuery = true)
  Page<PostsResponseEmp> findGroupByDate(@Param("date") String date, Pageable pageable);

  @Query(
      value =
          QUERY
              + "join tag2post on tag2post.post_id = posts.id "
              + "join tags on tags.id = tag2post.tag_id "
              + "where posts.is_active = 1 "
              + "and posts.moderation_status = 'ACCEPTED' "
              + "and posts.time <= now() "
              + "and tags.name like :tag "
              + "group by id",
      nativeQuery = true)
  Page<PostsResponseEmp> findGroupByTag(@Param("tag") String tag, Pageable pageable);


  @Query(
      value = "select year(time) as year, count(*) as count, date(time) as date "
          + "from posts "
          + "where is_active = 1 "
          + "and moderation_status = 'ACCEPTED' "
          + "and time <= now() "
          + "group by date(time)"
          + ";",
      nativeQuery = true)
  List<CalendarResponseEmp> calendar();

  @Query(
      value = "select posts.id id, "
          + "  posts.time as time, "
          + "  users.id as userId, "
          + "  users.name as userName, "
          + "  posts.title as title, "
          + "  posts.text as text, "
          + "  sum(case when post_votes.value = 1 then 1 else 0 end) as likes, "
          + "  sum(case when post_votes.value = -1 then 1 else 0 end) as dislikes, "
          + "  posts.view_count as views, "
          + "  group_concat(distinct tags.name order by tags.name asc) as tags "
          + "  from posts "
          + "  join users on users.id = posts.user_id "
          + "  left join post_votes on posts.id=post_votes.post_id "
          + "  left join tag2post on tag2post.post_id = posts.id "
          + "  left join tags on tags.id = tag2post.tag_id "
          + "  where posts.id = :id "
          + "  and posts.is_active = 1 "
          + "  and posts.moderation_status = 'ACCEPTED' "
          + "  and posts.time <= now();",
      nativeQuery = true)
  PostResponseEmp post(@Param("id") int id);


}
