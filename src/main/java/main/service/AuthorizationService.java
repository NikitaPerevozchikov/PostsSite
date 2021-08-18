package main.service;

import java.security.Principal;
import main.api.request.AuthorizationRequest;
import main.api.response.AuthorizationResponse;
import main.api.response.AuthorizationResponse.UserResponse;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {

  private final UsersRepository usersRepository;
  private final PostsRepository postsRepository;
  private final AuthenticationManager authenticationManager;

  @Autowired
  public AuthorizationService(
      UsersRepository usersRepository,
      PostsRepository postsRepository,
      AuthenticationManager authenticationManager) {
    this.usersRepository = usersRepository;
    this.postsRepository = postsRepository;
    this.authenticationManager = authenticationManager;
  }

  public AuthorizationResponse getCheckAuthorization(Principal principal) {
    if (principal == null) {
      return new AuthorizationResponse();
    }
    return completeAuthorizationResponse(principal.getName());
  }

  public AuthorizationResponse addAuthorizationUser(AuthorizationRequest authorizationRequest) {
    try {
      Authentication auth =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  authorizationRequest.getEmail(), authorizationRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(auth);
      User user = (User) auth.getPrincipal();
      return completeAuthorizationResponse(user.getUsername());
    } catch (Exception e) {
      return new AuthorizationResponse();
    }
  }

  public AuthorizationResponse deleteAuthorizationUser() {
    SecurityContextHolder.clearContext();
    AuthorizationResponse response = new AuthorizationResponse();
    response.setResult(true);
    return response;
  }

  private AuthorizationResponse completeAuthorizationResponse(String email) {
    main.models.User user = usersRepository.findByEmail(email);
    AuthorizationResponse response = new AuthorizationResponse();
    response.setResult(true);
    UserResponse userResponse =
        new UserResponse(
            user.getId(),
            user.getName(),
            user.getPhoto(),
            user.getEmail(),
            user.isModerator(),
            (user.isModerator() ? postsRepository.findByModStatusNEW().size() : 0),
            user.isModerator());
    response.setUserResponse(userResponse);
    return response;
  }
}
