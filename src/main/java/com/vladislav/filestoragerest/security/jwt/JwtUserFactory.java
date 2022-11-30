package com.vladislav.filestoragerest.security.jwt;

import com.vladislav.filestoragerest.model.Role;
import com.vladislav.filestoragerest.model.Status;
import com.vladislav.filestoragerest.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public final class JwtUserFactory {
    public JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getStatus().equals(Status.ACTIVE),
                user.getLastUpdated(),
                mapToGrantedAuthorities(new ArrayList<>(user.getRoles()))
        );
    }

    private static List<SimpleGrantedAuthority> mapToGrantedAuthorities(List<Role> userRoles) {
        return userRoles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .toList();
    }
}
