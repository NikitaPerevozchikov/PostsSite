package main.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "is_moderator", nullable = false, columnDefinition = "TINYINT(1)")
  private boolean isModerator;

  @Column(name = "reg_time", nullable = false, columnDefinition = "DATETIME")
  private LocalDateTime regTime;

  @Column(nullable = false, columnDefinition = "VARCHAR(255)")
  private String name;

  @Column(nullable = false, columnDefinition = "VARCHAR(255)")
  private String email;

  @Column(nullable = false, columnDefinition = "VARCHAR(255)")
  private String password;

  @Column(columnDefinition = "VARCHAR(255)")
  private String code;
  @Column(columnDefinition = "TEXT")
  private String photo;

  public Role getRole () {
    return isModerator ? Role.MODERATOR : Role.USER;
  }

  @JsonIgnore
  @OneToMany(mappedBy = "moderator", cascade = CascadeType.ALL)
  private List<Post> moderationPosts;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Post> posts;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<PostVote> postVotes;

  @JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<PostComment> comments;
}
