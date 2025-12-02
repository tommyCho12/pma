package com.pma.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
        @Autowired
        private CustomUserDetailsService customUserDetailsService;

        @Autowired
        BCryptPasswordEncoder bCryptPasswordEncoder;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
                // Use custom UserDetailsService for email-based authentication
                auth.userDetailsService(customUserDetailsService)
                                .passwordEncoder(bCryptPasswordEncoder);
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
                http
                                .authorizeRequests()
                                .antMatchers("/projects/new").hasRole("ADMIN")
                                .antMatchers("/projects/save").hasRole("ADMIN")
                                .antMatchers("/employees/new").hasRole("ADMIN")
                                .antMatchers("/employees/save").hasRole("ADMIN")
                                .antMatchers("/h2-console/**").permitAll()
                                .antMatchers("/", "/**").permitAll()
                                .and().formLogin()
                                .and().logout().permitAll()
                                .and().csrf().disable().headers().frameOptions().disable();
        }
}
