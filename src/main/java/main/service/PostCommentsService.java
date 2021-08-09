package main.service;

import java.security.Principal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import main.api.request.CommentRequest;
import main.api.response.CommentResponse;
import main.models.Post;
import main.models.PostComment;
import main.repository.PostCommentsRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PostCommentsService {

  private final PostCommentsRepository postCommentsRepository;
  private final PostsRepository postsRepository;
  private final UsersRepository usersRepository;

  @Autowired
  public PostCommentsService(
      PostCommentsRepository postCommentsRepository,
      PostsRepository postsRepository,
      UsersRepository usersRepository) {
    this.postCommentsRepository = postCommentsRepository;
    this.postsRepository = postsRepository;
    this.usersRepository = usersRepository;
  }

  public ResponseEntity<?> createComment(CommentRequest request, Principal principal) {

    main.models.User user;
    try {
      user = usersRepository.findByEmail(principal.getName());
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    Post post = postsRepository.findByPostId(request.getPostId());

    if (post == null
        || (request.getParentId() != null
            && !postCommentsRepository.existsById(request.getParentId()))) {
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    CommentResponse response = new CommentResponse();
    PostComment comment = new PostComment();

    if (request.getText().length() > 49) {
      comment.setText(request.getText());
    } else {
      response.getErrors().put("text", "Текст публикации слишком короткий");
    }

    if (response.getErrors().isEmpty()) {
      comment.setUser(user);
      comment.setPost(post);
      comment.setParentId(request.getParentId());

      comment.setTime(
          LocalDateTime.ofInstant(
              Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.of("UTC")));

      response.setId(postCommentsRepository.save(comment).getId());

      return new ResponseEntity<>(response, HttpStatus.OK);
    } else {
      response.setResult(false);
      return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
  }
}
