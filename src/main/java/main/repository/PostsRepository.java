package main.repository;

import java.util.List;
import main.api.response.calendarResponse.CalendarResponseEmp;
import main.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostsRepository extends CrudRepository<Post, Integer> {

  @Query(
      value =
          "select * from posts "
              + "where is_active = 1 "
              + "and moderation_status = 'ACCEPTED' "
              + "and time <= now() "
              + "group by id ",
      nativeQuery = true)
  Page<Post> findGroupById(Pageable pageable);

  @Query(
      value =
          "select * from posts as p "
              + "left join post_comments as pc on pc.post_id = p.id "
              + "where p.is_active = 1 "
              + "and p.moderation_status = 'ACCEPTED' "
              + "and p.time <= now() "
              + "group by p.id "
              + "order by count(pc.id) desc ",
      nativeQuery = true)
  Page<Post> findGroupByComments(Pageable pageable);

  @Query(
      value =
          "select *from posts as p "
              + "left join post_votes as pv on pv.post_id = p.id "
              + "where p.is_active = 1 "
              + "and p.moderation_status = 'ACCEPTED' "
              + "and p.time <= now() "
              + "group by p.id "
              + "order by sum(case when pv.value=1 then 1 else 0 end) desc ",
      nativeQuery = true)
  Page<Post> findGroupByLikes(Pageable pageable);

  @Query(
      value =
          "select * from posts "
              + "where is_active = 1 "
              + "and moderation_status = 'ACCEPTED' "
              + "and time <= now() "
              + "and title like concat('%',:query,'%') or "
              + "text like concat('%',:query,'%') "
              + "order by time desc ",
      nativeQuery = true)
  Page<Post> findPostsByQuery(@Param("query") String query, Pageable pageable);

  @Query(
      value =
          "select * from posts "
              + "where is_active = 1 "
              + "and moderation_status = 'ACCEPTED' "
              + "and time <= now() "
              + "and date(time) = :date "
              + "order by time desc ",
      nativeQuery = true)
  Page<Post> findGroupByDate(@Param("date") String date, Pageable pageable);

  @Query(
      value =
          "select * from posts as p "
              + "join tag2post as t2p on t2p.post_id = p.id "
              + "join tags as t on t.id = t2p.tag_id "
              + "where p.is_active = 1 "
              + "and p.moderation_status = 'ACCEPTED' "
              + "and p.time <= now() "
              + "and t.name = :tag "
              + "order by p.time desc ",
      nativeQuery = true)
  Page<Post> findGroupByTag(@Param("tag") String tag, Pageable pageable);

  @Modifying
  @Query(
      value = "update posts " + "set view_count = view_count + 1 " + "where id = :id ;",
      nativeQuery = true)
  void incrementView(@Param("id") int id);

  @Query(
      value =
          "select year(time) as year, count(*) as count, date(time) as date "
              + "from posts "
              + "where is_active = 1 "
              + "and moderation_status = 'ACCEPTED' "
              + "and time <= now() "
              + "group by date(time);",
      nativeQuery = true)
  List<CalendarResponseEmp> findPostsByDate();

  @Query(value = "select * from posts where moderation_status = 'NEW';", nativeQuery = true)
  List<Post> findByModStatusNEW();

  @Query(
      value =
          "select * from posts "
              + "where is_active = 0 "
              + "and user_id = :userId "
              + "order by id ",
      nativeQuery = true)
  Page<Post> findByUserIdInactive(@Param("userId") int userId, Pageable pageable);

  @Query(
      value =
          "select * from posts "
              + "where is_active = 1 "
              + "and moderation_status = 'NEW' "
              + "and user_id = :userId "
              + "order by id ",
      nativeQuery = true)
  Page<Post> findByUserIdPending(@Param("userId") int userId, Pageable pageable);

  @Query(
      value =
          "select * from posts "
              + "where is_active = 1 "
              + "and moderation_status = 'DECLINED' "
              + "and user_id = :userId "
              + "order by id ",
      nativeQuery = true)
  Page<Post> findByUserIdDeclined(@Param("userId") int userId, Pageable pageable);

  @Query(
      value =
          "select * from posts "
              + "where is_active = 1 "
              + "and moderation_status = 'ACCEPTED' "
              + "and user_id = :userId "
              + "order by id ",
      nativeQuery = true)
  Page<Post> findByUserIdPublished(@Param("userId") int userId, Pageable pageable);
}
