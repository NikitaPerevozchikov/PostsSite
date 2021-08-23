package main.service;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import main.api.request.ModerationRequest;
import main.api.request.PostRequest;
import main.api.request.VoteRequest;
import main.api.response.GetPostResponse;
import main.api.response.GetPostResponse.Comment;
import main.api.response.GetPostResponse.Comment.UserComment;
import main.api.response.GetPostResponse.UserPost;
import main.api.response.PostResponse;
import main.api.response.PostsResponse;
import main.api.response.PostsResponse.Post.User;
import main.api.response.StatisticResponse;
import main.exceptions.ExceptionNotFound;
import main.exceptions.ExceptionUnauthorized;
import main.models.ModerationStatus;
import main.models.Post;
import main.models.PostVote;
import main.models.Tag;
import main.repository.GlobalSettingsRepository;
import main.repository.PostVotesRepository;
import main.repository.PostsRepository;
import main.repository.TagRepository;
import main.repository.UsersRepository;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostsService {

  private final PostsRepository postsRepository;
  private final UsersRepository usersRepository;
  private final TagRepository tagRepository;
  private final PostVotesRepository postVotesRepository;
  private final GlobalSettingsRepository globalSettingsRepository;

  @Autowired
  public PostsService(
      PostsRepository postsRepository,
      UsersRepository usersRepository,
      TagRepository tagRepository,
      PostVotesRepository postVotesRepository,
      GlobalSettingsRepository globalSettingsRepository) {
    this.postsRepository = postsRepository;
    this.usersRepository = usersRepository;
    this.tagRepository = tagRepository;
    this.postVotesRepository = postVotesRepository;
    this.globalSettingsRepository = globalSettingsRepository;
  }

  public PostsResponse getGroupPosts(Integer offset, Integer limit, String mode) {
    Page<Post> posts =
        (mode.equals("popular")
            ? postsRepository.findGroupByComments(PageRequest.of(offset, limit, Sort.unsorted()))
            : mode.equals("best")
                ? postsRepository.findGroupByComments(
                    PageRequest.of(offset, limit, Sort.unsorted()))
                : mode.equals("early")
                    ? postsRepository.findGroupById(PageRequest.of(offset, limit, Sort.by("time")))
                    : postsRepository.findGroupById(
                        PageRequest.of(offset, limit, Sort.by("time").descending())));

    return competePostsResponse(posts);
  }

  public PostsResponse getPostsByQuery(int offset, int limit, String query) {
    if (query.trim().isEmpty()) {
      return getGroupPosts(offset, limit, "recent");
    }
    Page<Post> posts = postsRepository.findPostsByQuery(query, PageRequest.of(offset, limit));
    return competePostsResponse(posts);
  }

  public PostsResponse getGroupByDate(Integer offset, Integer limit, String date) {

    Page<Post> posts = postsRepository.findGroupByDate(date, PageRequest.of(offset, limit));
    return competePostsResponse(posts);
  }

  public PostsResponse getGroupByTag(Integer offset, Integer limit, String tag) {

    Page<Post> posts = postsRepository.findGroupByTag(tag, PageRequest.of(offset, limit));
    return competePostsResponse(posts);
  }

  public GetPostResponse getPost(int id) {
    Post post = postsRepository.findByPostId(id);
    if (post == null) {
      throw new ExceptionNotFound();
    } else {
      postsRepository.incrementView(id);
      GetPostResponse response = new GetPostResponse();
      response.setId(post.getId());
      response.setTimestamp(
          post.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000);
      response.setActive(true);
      response.setUser(new UserPost(post.getUser().getId(), post.getUser().getName()));
      response.setTitle(post.getTitle());
      response.setText(post.getText());
      post.getPostVotes()
          .forEach(
              v -> {
                if (v.getValue() == 1) {
                  response.setLikeCount(response.getLikeCount() + 1);
                } else {
                  response.setDislikeCount(response.getDislikeCount() + 1);
                }
              });
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

      return response;
    }
  }

  public PostsResponse getMyPosts(
      Integer offset, Integer limit, String status, Principal principal) {
    main.models.User user = changeAuthorization(principal, "user");
    Page<Post> posts =
        (status.equals("inactive")
            ? postsRepository.findByUserIdInactive(user.getId(), PageRequest.of(offset, limit))
            : status.equals("pending")
                ? postsRepository.findByUserIdPending(user.getId(), PageRequest.of(offset, limit))
                : status.equals("declined")
                    ? postsRepository.findByUserIdDeclined(
                        user.getId(), PageRequest.of(offset, limit))
                    : postsRepository.findByUserIdPublished(
                        user.getId(), PageRequest.of(offset, limit)));

    return competePostsResponse(posts);
  }

  public PostsResponse getPostsForMod(
      Integer offset, Integer limit, String status, Principal principal) {
    main.models.User user = changeAuthorization(principal, "moderator");
    Page<Post> posts =
        (status.equals("declined")
            ? postsRepository.findMyModPostByDeclined(user.getId(), PageRequest.of(offset, limit))
            : status.equals("accepted")
                ? postsRepository.findMyModPostByAccepted(
                    user.getId(), PageRequest.of(offset, limit))
                : postsRepository.findByModStatusNew(user.getId(), PageRequest.of(offset, limit)));

    return competePostsResponse(posts);
  }

  public PostResponse addPost(PostRequest request, Principal principal) {
    main.models.User user = changeAuthorization(principal, "user");
    Post post = new Post();
    post.setUser(user);
    return completePostResponse(request, post);
  }

  public PostResponse updatePost(int id, PostRequest request, Principal principal) {
    main.models.User user = changeAuthorization(principal, "user");
    Post post = postsRepository.findByPostId(id);
    if (post == null) {
      throw new ExceptionNotFound();
    }
    return completePostResponse(request, post);
  }

  public PostResponse updateModStatus(ModerationRequest request, Principal principal) {
    main.models.User user = changeAuthorization(principal, "moderator");
    PostResponse response = new PostResponse();
    if (postsRepository.findByPostId(request.getPostId()) != null) {
      if (request.getDecision().equals("accept")) {
        postsRepository.createModStatusAccept(request.getPostId(), user.getId());
        response.setResult(true);
      }
      if (request.getDecision().equals("decline")) {
        postsRepository.createModStatusDecline(request.getPostId(), user.getId());
        response.setResult(true);
      }
    }
    return response;
  }

  public PostResponse addVotes(VoteRequest request, Principal principal, String type) {
    main.models.User user = changeAuthorization(principal, "user");
    PostResponse response = new PostResponse();
    PostVote postVote = postVotesRepository.findByUserIdAndPostId(user.getId(), request.getPostId());
    if (postVote == null && type.equals("like")) {
      postVotesRepository.makeLike(user.getId(), request.getPostId(), 1);
      response.setResult(true);
    }
    if (postVote == null && type.equals("dislike")) {
      postVotesRepository.makeLike(user.getId(), request.getPostId(), -1);
      response.setResult(true);
    }
    return response;
  }

  public StatisticResponse getMyStatistic(Principal principal) {
    main.models.User user = changeAuthorization(principal, "user");
    List<Post> posts = postsRepository.findByUserIdWActive(user.getId());
    return completeStatisticResponse(posts);
  }

  public StatisticResponse getAllStatistic(Principal principal) {
    main.models.User user = changeAuthorization(principal, "user");
    if (globalSettingsRepository.findValueByCode("STATISTICS_IS_PUBLIC").equals("YES")
        || user.isModerator()) {
      List<Post> posts = postsRepository.findAllWActive();
      return completeStatisticResponse(posts);
    } else {
      throw new ExceptionUnauthorized();
    }
  }

  private StatisticResponse completeStatisticResponse(List<Post> posts) {
    StatisticResponse response = new StatisticResponse();
    posts.forEach(
        e -> {
          response.setPostsCount(posts.size());
          response.setViewsCount(response.getViewsCount() + e.getViewCount());
          e.getPostVotes()
              .forEach(
                  v -> {
                    if (v.getValue() == 1) {
                      response.setLikesCount(response.getLikesCount() + 1);
                    } else {
                      response.setDislikesCount(response.getDislikesCount() + 1);
                    }
                  });
          long currentTime = e.getTime().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli() / 1000;
          response.setFirstPublication(
              response.getFirstPublication() == 0
                  ? currentTime
                  : Math.min(response.getFirstPublication(), currentTime));
        });
    return response;
  }

  private PostResponse completePostResponse(PostRequest request, Post post) {
    PostResponse response = new PostResponse();
    if (request.getTitle().length() > 2) {
      post.setTitle(request.getTitle());
    } else {
      response.getErrors().put("title", "Заголовок не установлен");
    }
    if (request.getText().length() > 49) {
      post.setText(request.getText());
    } else {
      response.getErrors().put("text", "Текст публикации слишком короткий");
    }
    if (response.getErrors().isEmpty()) {
      post.setTime(
          LocalDateTime.ofInstant(
              Instant.ofEpochMilli(
                  request.getTimestamp() * 1000 > System.currentTimeMillis()
                      ? System.currentTimeMillis()
                      : request.getTimestamp() * 1000),
              ZoneId.of("UTC")));

      post.setActive(request.getActive() == 1);
      if (!request.getTags().isEmpty()) {
        request
            .getTags()
            .forEach(
                e -> {
                  if (tagRepository.findByName(e) == null) {
                    tagRepository.createNewTag(e);
                  }
                  if (post.getTags() == null) {
                    post.setTags(new HashSet<>());
                  }
                  post.getTags().add(tagRepository.findByName(e));
                });
      }
      post.setModerationStatus(
          globalSettingsRepository.findValueByCode("POST_PREMODERATION").equals("YES")
              ? ModerationStatus.NEW
              : ModerationStatus.ACCEPTED);
      postsRepository.save(post);
      response.setResult(true);
    }
    return response;
  }

  private PostsResponse competePostsResponse(Page<Post> posts) {
    PostsResponse response = new PostsResponse();
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
          post.setAnnounce((announce.length() > 150 ? announce.substring(150) : announce) + "...");
          e.getPostVotes()
              .forEach(
                  v -> {
                    if (v.getValue() == 1) {
                      post.setLikeCount(post.getLikeCount() + 1);
                    } else {
                      post.setDislikeCount(post.getDislikeCount() + 1);
                    }
                  });
          post.setCommentCount(e.getComments().size());
          post.setViewCount(e.getViewCount());
          postList.add(post);
        });
    response.setPosts(postList);
    return response;
  }

  private main.models.User changeAuthorization(Principal principal, String typeUser) {
    main.models.User user;
    try {
      if (typeUser.equals("moderator")) {
        user = usersRepository.findByEmailAndMod(principal.getName());
      } else {
        user = usersRepository.findByEmail(principal.getName());
      }
    } catch (Exception e) {
      throw new ExceptionUnauthorized();
    }
    if (user == null) {
      throw new ExceptionUnauthorized();
    }
    return user;
  }
}
