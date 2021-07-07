package main.models;

import com.sun.istack.NotNull;
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
@Table(name = "post_votes")
@Getter
@Setter
public class PostVote {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @JoinColumn(name = "user_id", insertable = false, updatable = false, nullable = false,
      columnDefinition = "INT")
  @ManyToOne(cascade = CascadeType.ALL)
  private User user;

  @JoinColumn(name = "post_id", insertable = false, updatable = false, nullable = false,
      columnDefinition = "INT")
  @ManyToOne(cascade = CascadeType.ALL)
  private Post post;

  @Column(nullable = false, columnDefinition = "DATETIME")
  private LocalDateTime time;

  @Column(nullable = false, columnDefinition = "TINYINT")
  private boolean value;
}
