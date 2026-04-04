package com.finance.dashboard.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.finance.dashboard.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)   // stores "ADMIN" not 0,1,2 in the DB
    @Column(nullable = false)
    private Role role;

    @JsonProperty("active")
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_ADMIN", "ROLE_VIEWER" etc. — Spring expects the "ROLE_" prefix
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override public String getPassword()   { return passwordHash; }
    @Override public String getUsername()   { return email; }
    @Override public boolean isAccountNonExpired()   { return true; }
    @Override public boolean isAccountNonLocked()    { return active; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()             { return active; }
}
