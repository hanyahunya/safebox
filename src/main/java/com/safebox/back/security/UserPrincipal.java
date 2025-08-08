package com.safebox.back.security;

import com.safebox.back.user.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class UserPrincipal implements UserDetails {
    @Getter
    private final String userId;

    @Getter
    private final Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // 추후 권한 부여 할수도 있음 ( 지금은 일단 X )
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
