package com.vladislav.filestoragerest.config;

import com.vladislav.filestoragerest.security.jwt.JwtConfigurer;
import com.vladislav.filestoragerest.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    public SecurityConfig(@Lazy JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private static final String ADMIN_ENDPOINT = "/api/v1/admin/**";
    private static final String MODERATOR_ENDPOINT = "/api/v1/moderator/**";
    private static final String USER_ENDPOINT = "/api/v1/users/**";
    private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(LOGIN_ENDPOINT).permitAll()
                .antMatchers(USER_ENDPOINT).hasAnyRole("USER", "MODERATOR", "ADMIN")
                .antMatchers(MODERATOR_ENDPOINT).hasAnyRole("MODERATOR", "ADMIN")
                .antMatchers(ADMIN_ENDPOINT).hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));

    }
}
