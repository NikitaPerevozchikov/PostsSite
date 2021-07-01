package main.service;

import lombok.Getter;
import lombok.Setter;
import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
public class TagsService {

  @Autowired
  TagRepository tagRepository;

}
