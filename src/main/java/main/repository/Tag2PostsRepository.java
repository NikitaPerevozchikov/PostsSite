package main.repository;

import java.util.List;
import main.api.response.Tag2PostsResponse.Tag2Posts;
import main.models.Tag2Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag2PostsRepository extends CrudRepository<Tag2Post, Integer> {

  @Query(
      value =
          "select tags.name as name, "
              + "round (count(*)/(select count(*) from posts)*"
              + "(select 1/max(weight)from (select (count(*)/(select count(*) from posts)) as "
              + "weight from tag2post group by tag_id) as maxWeight),2) as weight "
              + "from tag2post "
              + "join tags on tags.id=tag2post.tag_id "
              + "group by tags.name"
              + ";",
      nativeQuery = true)
  List<Tag2Posts> getGroupByTags();

  @Query(
      value =
          "select tags.name as name, "
              + "round (count(*)/(select count(*) from posts)*"
              + "(select 1/max(weight)from (select (count(*)/(select count(*) from posts)) as "
              + "weight from tag2post group by tag_id) as maxWeight),2) as weight "
              + "from tag2post "
              + "join tags on tags.id=tag2post.tag_id "
              + "where name = :query "
              + "group by tags.name"
              + ";",
      nativeQuery = true)
  List<Tag2Posts> getGroupByTags(String query);
}
