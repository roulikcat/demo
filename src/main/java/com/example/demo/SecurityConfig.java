package com.example.demo;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfiguration {

    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .requestMatchers("/login/oauth2/code/wso2/**").permitAll() // Allow the OAuth2 callback endpoint
                .anyRequest().authenticated() // Secure other endpoints
            .and()
            .oauth2Login()
                .loginPage("/login") // Specify login page if needed
                .authorizationEndpoint()
                    .baseUri("/oauth2/authorization") // OAuth2 authorization endpoint
                .and()
                .redirectionEndpoint()
                    .baseUri("/login/oauth2/code/*"); // OAuth2 callback endpoint
    }
}