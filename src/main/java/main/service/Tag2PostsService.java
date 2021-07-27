package main.service;

import main.api.response.Tag2PostsResponse;
import main.repository.Tag2PostsRepository;
import org.springframework.stereotype.Service;

@Service
public class Tag2PostsService {

  private final Tag2PostsRepository tag2PostsRepository;

  public Tag2PostsService(Tag2PostsRepository tag2PostsRepository) {
    this.tag2PostsRepository = tag2PostsRepository;
  }

  public Tag2PostsResponse getGroupByTags(String query) {
    Tag2PostsResponse tag2PostsResponse = new Tag2PostsResponse();
    tag2PostsResponse.setTags(
        query == null
            ? tag2PostsRepository.getGroupByTags()
            : tag2PostsRepository.getGroupByTags(query));
    return tag2PostsResponse;
  }
}
