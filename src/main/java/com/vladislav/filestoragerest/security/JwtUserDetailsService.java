package com.vladislav.filestoragerest.security;

import com.vladislav.filestoragerest.model.User;
import com.vladislav.filestoragerest.security.jwt.JwtUser;
import com.vladislav.filestoragerest.security.jwt.JwtUserFactory;
import com.vladislav.filestoragerest.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtUserDetailsService implements UserDetailsService {
    private UserService userService;

    public JwtUserDetailsService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }

        JwtUser jwtUser = JwtUserFactory.create(user);
        log.info("IN loadUserByUsername - user with username {} successfully loaded", username);
        return jwtUser;
    }

}
