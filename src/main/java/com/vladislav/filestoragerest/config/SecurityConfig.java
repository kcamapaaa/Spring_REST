package com.vladislav.filestoragerest.config;

import com.vladislav.filestoragerest.security.jwt.JwtConfigurer;
import com.vladislav.filestoragerest.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private JwtTokenProvider jwtTokenProvider;
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String MODERATOR_ROLE = "MODERATOR";
    private static final String USER_ROLE = "USER";
    @Autowired
    public SecurityConfig(@Lazy JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

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
                .antMatchers("/api/v1/auth/login").permitAll()
                .antMatchers("/api/v1/users/myInfo").hasAnyRole(USER_ROLE, MODERATOR_ROLE, ADMIN_ROLE)
                .antMatchers("/api/v1/files/**").hasAnyRole(MODERATOR_ROLE, ADMIN_ROLE)
                .antMatchers(HttpMethod.GET, "/api/v1/users/**").hasAnyRole(MODERATOR_ROLE, ADMIN_ROLE)
                .antMatchers("/api/v1/users/**").hasRole(ADMIN_ROLE)
                .antMatchers("/api/v1/**").hasAnyRole(MODERATOR_ROLE, ADMIN_ROLE)
                .anyRequest()
                .authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));

    }
}
