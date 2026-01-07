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

                                // API endpoints - should come FIRST
                                .antMatchers("/api/documents/*/review").authenticated() // or
                                                                                        // .hasAuthority("PERMISSION_REVIEWER")
                                .antMatchers("/api/documents/**").permitAll() // or .authenticated() if all API calls
                                                                              // need auth

                                // Web endpoints
                                .antMatchers("/projects/new").hasRole("ADMIN")
                                .antMatchers("/projects/save").hasRole("ADMIN")
                                .antMatchers("/employees/new").hasRole("ADMIN")
                                .antMatchers("/employees/save").hasRole("ADMIN")
                                .antMatchers("/documents/new").authenticated()
                                .antMatchers("/documents/save").authenticated()
                                .antMatchers("/documents/update/*").authenticated()
                                .antMatchers("/documents/delete/*").authenticated()
                                .antMatchers("/h2-console/**").permitAll()

                                // Catch-all - must be LAST
                                .antMatchers("/", "/**").permitAll()
                                .and().httpBasic() // Enable HTTP Basic authentication for API testing
                                .and().formLogin()
                                .and().logout().permitAll()
                                .and().csrf().disable().headers().frameOptions().disable();
        }
}
