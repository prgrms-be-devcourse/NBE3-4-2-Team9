package com.backend.global.security.custom;

import com.backend.domain.user.entity.SiteUser;
import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final SiteUser siteUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // SiteUser에서 역할 뽑아 SimpleGranted 변환
        authorities.add(new SimpleGrantedAuthority(siteUser.getUserRole()));

        return authorities;
    }

    public SiteUser getSiteUser() {
        return siteUser;
    }

    public Long getId() {
        return siteUser.getId();
    }

    @Override
    public String getUsername() {
        return siteUser.getEmail();
    }

    @Override
    public String getPassword() {
        return siteUser.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
