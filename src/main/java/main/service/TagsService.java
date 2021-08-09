package main.service;

import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagsService {

  private final TagRepository tagRepository;

  @Autowired
  public TagsService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }
}
