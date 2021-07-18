package main.service;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import main.api.response.calendarResponse.CalendarResponse;
import main.api.response.postResponse.PostResponse;
import main.api.response.postResponse.PostResponse.Comment;
import main.api.response.postResponse.PostResponse.Comment.UserComment;
import main.api.response.postResponse.PostResponse.UserPost;
import main.api.response.postResponse.PostResponseEmp;
import main.api.response.postsResponse.PostsResponse;
import main.api.response.postsResponse.PostsResponse.Post.User;
import main.api.response.postsResponse.PostsResponseEmp;
import main.repository.PostCommentsRepository;
import main.repository.PostVotesRepository;
import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PostsService {

  @Autowired
  PostsRepository postsRepository;
  @Autowired
  PostVotesRepository postVotesRepository;
  @Autowired
  PostCommentsRepository postCommentsRepository;
  SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

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
        sort = Sort.by("comments").descending();
        break;
      }
      case "best": {
        sort = Sort.by("likes").descending();
        break;
      }
      case "early": {
        sort = Sort.by("time");
        break;
      }
      default:
        sort = Sort.by("time").descending();
    }
    Page<PostsResponseEmp> posts =
        postsRepository.findGroupById(PageRequest.of(offset, limit, sort));
    competeResponse(response, posts);
    return response;
  }

  public PostsResponse search(int offset, int limit, String query) {
    if (query.trim().isEmpty()) {
      return getGroupPosts(offset, limit, "recent");
    }
    Page<PostsResponseEmp> posts = postsRepository.search(query, PageRequest.of(offset, limit));
    PostsResponse response = new PostsResponse();
    competeResponse(response, posts);
    return response;
  }

  public CalendarResponse calendar(String year) {
    Set<String> years = new HashSet<>();
    Map<String, Integer> posts = new HashMap<>();
    CalendarResponse response = new CalendarResponse();
    postsRepository
        .calendar()
        .forEach(
            e -> {
              years.add(e.getYear());
              if (e.getYear().equals(year == null ? Year.now().toString() : year)) {
                posts.put(format.format(e.getDate()), e.getCount());
              }
            });
    response.setYears(years);
    response.setPosts(posts);
    return response;
  }

  public PostsResponse getGroupDate(Integer offset, Integer limit, String date) {

    if (offset == null) {
      offset = 0;
    }
    if (limit == null) {
      limit = 10;
    }

    PostsResponse response = new PostsResponse();
    Page<PostsResponseEmp> posts =
        postsRepository.findGroupByDate(date, PageRequest.of(offset, limit, Sort.by("time")));
    competeResponse(response, posts);
    return response;
  }

  public PostsResponse getGroupTag(Integer offset, Integer limit, String tag) {

    if (offset == null) {
      offset = 0;
    }
    if (limit == null) {
      limit = 10;
    }

    PostsResponse response = new PostsResponse();
    Page<PostsResponseEmp> posts =
        postsRepository.findGroupByTag(tag, PageRequest.of(offset, limit, Sort.by("time")));
    competeResponse(response, posts);
    return response;
  }

  public ResponseEntity<?> post(int id) {
    PostResponse response = new PostResponse();
    PostResponseEmp post = postsRepository.post(id);

    if (post.getId() == null) {
      return new ResponseEntity<> (HttpStatus.NOT_FOUND);
    }

    List<Comment> comments = postCommentsRepository.getCommentsByPostId(id).stream()
        .map(e -> new Comment(
            e.getId(),
            e.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000,
            e.getText(),
            new UserComment(e.getUserId(), e.getUserName(), e.getUSerPhoto())))
        .collect(Collectors.toList());



    response.setId(post.getId());
    response
        .setTimestamp(post.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000);
    response.setActive(true);
    response.setUser(new UserPost(post.getUserId(), post.getUserName()));
    response.setTitle(post.getTitle());
    response.setText(post.getText());
    response.setLikeCount(post.getLikes());
    response.setDislikeCount(post.getDislikes());
    response.setViewCount(post.getViews());
    response.setComments(comments);
    response.setTags(post.getTags());

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private void competeResponse(PostsResponse response, Page<PostsResponseEmp> posts) {
    response.setCount(posts.getNumberOfElements());
    List<PostsResponse.Post> postList = new ArrayList<>();
    posts.forEach(
        e -> {
          PostsResponse.Post post = new PostsResponse.Post();
          post.setId(e.getId());
          post.setTimestamp(e.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000);
          post.setUser(new User(e.getUserId(), e.getUserName()));
          post.setTitle(e.getTitle());
          post.setAnnounce(
              e.getText().length() > 150 ? e.getText().substring(150) : e.getText() + "...");
          post.setLikeCount(e.getLikes());
          post.setDislikeCount(e.getDislikes());
          post.setCommentCount(e.getComments());
          post.setViewCount(e.getViews());
          postList.add(post);
        });
    response.setPosts(postList);
  }
}
