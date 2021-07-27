package main.service;

import java.security.Principal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import main.api.response.PostResponse;
import main.api.response.PostResponse.Comment;
import main.api.response.PostResponse.Comment.UserComment;
import main.api.response.PostResponse.UserPost;
import main.api.response.PostsResponse;
import main.api.response.PostsResponse.Post.User;
import main.models.Post;
import main.models.Tag;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostsService {

  private final PostsRepository postsRepository;
  private final UsersRepository usersRepository;

  public PostsService(PostsRepository postsRepository, UsersRepository usersRepository) {
    this.postsRepository = postsRepository;
    this.usersRepository = usersRepository;
  }

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
    Page<Post> posts;

    switch (mode) {
      case "popular":
        {
          posts =
              postsRepository.findGroupByComments(PageRequest.of(offset, limit, Sort.unsorted()));
          break;
        }
      case "best":
        {
          posts = postsRepository.findGroupByLikes(PageRequest.of(offset, limit, Sort.unsorted()));
          break;
        }
      case "early":
        {
          posts = postsRepository.findGroupById(PageRequest.of(offset, limit, Sort.by("time")));
          break;
        }
      default:
        posts =
            postsRepository.findGroupById(
                PageRequest.of(offset, limit, Sort.by("time").descending()));
    }

    competeResponse(response, posts);
    return response;
  }

  public PostsResponse getPostsByQuery(int offset, int limit, String query) {
    if (query.trim().isEmpty()) {
      return getGroupPosts(offset, limit, "recent");
    }
    Page<Post> posts = postsRepository.findPostsByQuery(query, PageRequest.of(offset, limit));
    PostsResponse response = new PostsResponse();
    competeResponse(response, posts);
    return response;
  }

  public PostsResponse getGroupByDate(Integer offset, Integer limit, String date) {

    if (offset == null) {
      offset = 0;
    }
    if (limit == null) {
      limit = 10;
    }

    PostsResponse response = new PostsResponse();
    Page<Post> posts = postsRepository.findGroupByDate(date, PageRequest.of(offset, limit));
    competeResponse(response, posts);
    return response;
  }

  public PostsResponse getGroupByTag(Integer offset, Integer limit, String tag) {

    if (offset == null) {
      offset = 0;
    }
    if (limit == null) {
      limit = 10;
    }

    PostsResponse response = new PostsResponse();
    Page<Post> posts = postsRepository.findGroupByTag(tag, PageRequest.of(offset, limit));
    competeResponse(response, posts);
    return response;
  }

  public ResponseEntity<?> getPost(int id) {
    postsRepository.incrementView(id);
    PostResponse response = new PostResponse();
    Optional<Post> optionalPost = postsRepository.findById(id);

    if (optionalPost.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    } else {
      Post post = optionalPost.get();
      response.setId(post.getId());
      response.setTimestamp(
          post.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000);
      response.setActive(true);
      response.setUser(new UserPost(post.getUser().getId(), post.getUser().getName()));
      response.setTitle(post.getTitle());
      response.setText(post.getText());
      AtomicInteger like = new AtomicInteger();
      AtomicInteger disLike = new AtomicInteger();
      post.getPostVotes()
          .forEach(
              v -> {
                if (v.isValue()) {
                  like.getAndIncrement();
                } else {
                  disLike.getAndIncrement();
                }
              });
      response.setLikeCount(like.get());
      response.setDislikeCount(disLike.get());
      response.setViewCount(post.getViewCount());
      response.setComments(
          post.getComments().stream()
              .map(
                  e ->
                      new Comment(
                          e.getId(),
                          e.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000,
                          e.getText(),
                          new UserComment(
                              e.getUser().getId(), e.getUser().getName(), e.getUser().getPhoto())))
              .collect(Collectors.toList()));

      response.setTags(post.getTags().stream().map(Tag::getName).toArray(String[]::new));

      return new ResponseEntity<>(response, HttpStatus.OK);
    }
  }

  public PostsResponse getMyPosts(
      Integer offset, Integer limit, String status, Principal principal) {
    int userId = usersRepository.findByEmail(principal.getName()).getId();

    if (offset == null) {
      offset = 0;
    }
    if (limit == null) {
      limit = 10;
    }
    if (status == null) {
      status = "published";
    }
    PostsResponse response = new PostsResponse();
    Page<Post> posts;

    switch (status) {
      case "inactive":
        {
          posts = postsRepository.findByUserIdInactive(userId, PageRequest.of(offset, limit));
          break;
        }
      case "pending":
        {
          posts = postsRepository.findByUserIdPending(userId, PageRequest.of(offset, limit));
          break;
        }
      case "declined":
        {
          posts = postsRepository.findByUserIdDeclined(userId, PageRequest.of(offset, limit));
          break;
        }
      default:
        posts = postsRepository.findByUserIdPublished(userId, PageRequest.of(offset, limit));
    }

    competeResponse(response, posts);
    return response;
  }

  private void competeResponse(PostsResponse response, Page<Post> posts) {
    response.setCount(posts.getNumberOfElements());
    List<PostsResponse.Post> postList = new ArrayList<>();
    posts.forEach(
        e -> {
          PostsResponse.Post post = new PostsResponse.Post();
          post.setId(e.getId());
          post.setTimestamp(e.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000);
          post.setUser(new User(e.getUser().getId(), e.getUser().getName()));
          post.setTitle(e.getTitle());
          String announce = Jsoup.clean(e.getText(), Whitelist.none());
          post.setAnnounce(
              (announce.length() > 150 ? announce.substring(150) : e.getText()) + "...");

          AtomicInteger like = new AtomicInteger();
          AtomicInteger disLike = new AtomicInteger();

          e.getPostVotes()
              .forEach(
                  v -> {
                    if (v.isValue()) {
                      like.getAndIncrement();
                    } else {
                      disLike.getAndIncrement();
                    }
                  });

          post.setLikeCount(like.get());
          post.setDislikeCount(disLike.get());
          post.setCommentCount(e.getComments().size());
          post.setViewCount(e.getViewCount());
          postList.add(post);
        });
    response.setPosts(postList);
  }
}
