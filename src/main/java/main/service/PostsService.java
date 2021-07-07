package main.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import main.api.response.PostsResponse;
import main.api.response.PostsResponse.Post.User;
import main.models.Post;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostsService {

  @Autowired
  private PostsRepository postsRepository;

  public PostsResponse getGroupPosts(Integer offset, Integer limit, String mode) {

    if (offset == null) {
      offset = 0;
    }
    if (limit == null) {
      limit = 10;
    }
    if (mode == null) {
      mode = "recent";
    }
    PostsResponse response = new PostsResponse();

    Sort sort;
    switch (mode) {

      case "popular": {
        sort = Sort.by("comment_count").descending();
        break;
      }
      case "best": {
        sort = Sort.by("like_count").descending();
        break;
      }
      case "early": {
        sort = Sort.by("time");
        break;
      }
      default:
        sort = Sort.by("time").descending();
    }
    Page<Post> posts = postsRepository.findGroupById(PageRequest.of(offset, limit, sort));
    competeResponse(response, posts);

    return response;
  }

  private void competeResponse(PostsResponse response, Page<Post> posts) {
    List<PostsResponse.Post> postList = new ArrayList<>();

    posts.forEach(e -> {
      PostsResponse.Post post = new PostsResponse.Post();
      post.setId(e.getId());
      post.setTimestamp(e.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000);
      post.setUser(new User(e.getUser().getId(), e.getUser().getName()));
      post.setTitle(e.getTitle());
      post.setAnnounce(
          e.getText().length() > 150 ? e.getText().substring(150) : e.getText() + "...");
      post.setLikeCount(e.getLikeCount());
      post.setDislikeCount(e.getDislikeCount());
      post.setCommentCount(e.getCommentCount());
      post.setViewCount(e.getViewCount());
      postList.add(post);
    });

    response.setPosts(postList);
  }

}
