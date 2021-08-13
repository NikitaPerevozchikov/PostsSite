package main.service;

import main.api.response.Tag2PostsResponse;
import main.repository.Tag2PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class Tag2PostsService {

  private final Tag2PostsRepository tag2PostsRepository;

  @Autowired
  public Tag2PostsService(Tag2PostsRepository tag2PostsRepository) {
    this.tag2PostsRepository = tag2PostsRepository;
  }

  public Tag2PostsResponse getGroupByTags(String query) {
    Tag2PostsResponse response = new Tag2PostsResponse();
    response.setTags(
        query == null
            ? tag2PostsRepository.getGroupByTags()
            : tag2PostsRepository.getGroupByTags(query));
    return response;
  }
}
