package main.models;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "post_comments")
@Getter
@Setter
public class PostComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "parent_id", columnDefinition = "INT")
  private Integer parentId;

  @JoinColumn(
      name = "post_id",
      updatable = false,
      nullable = false,
      columnDefinition = "INT")
  @ManyToOne(cascade = CascadeType.ALL)
  private Post post;

  @JoinColumn(
      name = "user_id",
      updatable = false,
      nullable = false,
      columnDefinition = "INT")
  @ManyToOne(cascade = CascadeType.ALL)
  private User user;

  @Column(nullable = false, columnDefinition = "DATETIME")
  private LocalDateTime time;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String text;
}
