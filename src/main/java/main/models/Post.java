package main.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "posts")
@Getter
@Setter
public class Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
  @ColumnDefault(value = "")
  private boolean isActive;

  @Column(name = "moderation_status", nullable = false)
  @Enumerated(EnumType.STRING)
  private ModerationStatus moderationStatus;

  @JoinColumn(
      name = "moderator_id",
      insertable = false,
      updatable = false,
      columnDefinition = "INT")
  @ManyToOne(cascade = CascadeType.ALL)
  private User moderator;

  @JoinColumn(
      name = "user_id",
      updatable = false,
      nullable = false,
      columnDefinition = "INT")
  @ManyToOne(cascade = CascadeType.ALL)
  private User user;

  @Column(name = "time", nullable = false, columnDefinition = "DATETIME")
  private LocalDateTime time;

  @Column(nullable = false, columnDefinition = "VARCHAR(255)")
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String text;

  @Column(name = "view_count", nullable = false, columnDefinition = "INT")
  private int viewCount;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<PostVote> postVotes;

  @ManyToMany()
  @JoinTable(
      name = "tag2post",
      joinColumns = {@JoinColumn(name = "tag_id")},
      inverseJoinColumns = {@JoinColumn(name = "post_id")})
  private Set<Tag> tags;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
  private List<PostComment> comments;
}
