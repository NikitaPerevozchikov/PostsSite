package main.service;

import main.repository.TagRepository;
import org.springframework.stereotype.Service;

@Service
public class TagsService {

  private final TagRepository tagRepository;

  public TagsService(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }
}
