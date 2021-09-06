package main.models;

import javax.persistence.CascadeType;
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
@Table(name = "tag2post")
@Getter
@Setter
public class Tag2Post {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @JoinColumn(name = "post_id", updatable = false, nullable = false, columnDefinition = "INT")
  @ManyToOne(cascade = CascadeType.ALL)
  private Post post;

  @JoinColumn(name = "tag_id", updatable = false, nullable = false, columnDefinition = "INT")
  @ManyToOne(cascade = CascadeType.ALL)
  private Tag tag;
}
