package main.service;

import main.repository.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  @Autowired
  private PostsRepository postsRepository;

}