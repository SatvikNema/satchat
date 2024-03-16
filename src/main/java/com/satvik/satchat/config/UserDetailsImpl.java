package com.satvik.satchat.config;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDetailsImpl implements UserDetails {

  private final UUID id;

  private String password;

  private final String username;
  private final String email;

  private final Set<GrantedAuthority> authorities;

  private final boolean accountNonExpired;

  private final boolean accountNonLocked;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserDetailsImpl that = (UserDetailsImpl) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  private final boolean credentialsNonExpired;

  private final boolean enabled;
}
