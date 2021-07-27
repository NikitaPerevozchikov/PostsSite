package main.models;

import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
  USER(Set.of(Permission.USER)),
  MODERATOR(Set.of(Permission.USER, Permission.MODERATE));

  private final Set<Permission> permissions;

  Role(Set<Permission> permissions) {
    this.permissions = permissions;
  }

  public Set<Permission> getPermissions() {
    return permissions;
  }

  public Set<SimpleGrantedAuthority> getAuthorities() {
    return permissions.stream()
        .map(e -> new SimpleGrantedAuthority(e.getPermission()))
        .collect(Collectors.toSet());
  }
}
