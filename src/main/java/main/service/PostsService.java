package main.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import main.api.response.PostsResponse;
import main.api.response.PostsResponse.Post.User;
import main.models.Post;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    Page<Post> postPage = postsRepository.findAll(PageRequest.of(offset, limit));
    response.setCount((int) postPage.getTotalElements());

    List<PostsResponse.Post> postList = new ArrayList<>();
    postPage.forEach(e -> {
      PostsResponse.Post post = new PostsResponse.Post();
      post.setId(e.getId());
      post.setTimestamp(e.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli());
      post.setUser(new User(e.getUser().getId(), e.getUser().getName()));
      post.setTitle(e.getTitle());
      post.setAnnounce(
          e.getText().length() > 150 ? e.getText().substring(150) : e.getText() + "...");

      AtomicInteger likeCount = new AtomicInteger();
      AtomicInteger dislikeCount = new AtomicInteger();

      e.getPostVotes()
          .forEach(

              c -> {

                if (c.isValue()) {
                  likeCount.getAndIncrement();
                } else {
                  dislikeCount.getAndIncrement();
                }
              });
      post.setLikeCount(likeCount.get());
      post.setDislikeCount(dislikeCount.get());
      post.setViewCount(e.getViewCount());
      postList.add(post);
    });

    switch (mode) {
      case "recent": {
        postList.sort(Comparator.comparing(PostsResponse.Post::getTimestamp));
        break;
      }
      case "popular": {
        postList.sort(Comparator.comparing(PostsResponse.Post::getCommentCount).reversed());
        break;
      }
      case "best": {
        postList.sort(Comparator.comparing(PostsResponse.Post::getLikeCount).reversed());
        break;
      }
      case "early": {
        postList.sort(Comparator.comparing(PostsResponse.Post::getTimestamp).reversed());
        break;
      }
    }
    response.setPosts(postList);
    return response;
  }

}
