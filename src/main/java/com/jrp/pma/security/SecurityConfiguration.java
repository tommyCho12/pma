package com.jrp.pma.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
    @Autowired
    DataSource dataSource;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(bCryptPasswordEncoder)
                .usersByUsernameQuery(
                        "select username, password, enabled " +
                        "from user_accounts " +
                        "where username = ?")
                .authoritiesByUsernameQuery(
                        "select username, role " +
                        "from user_accounts " +
                        "where username = ?")
                ;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/projects/new").hasRole("ADMIN")
                .antMatchers("/projects/save").hasRole("ADMIN")
                .antMatchers("/employees/new").hasRole("ADMIN")
                .antMatchers("/employees/save").hasRole("ADMIN")
                .antMatchers("/", "/**").permitAll()
                .and()
                .formLogin()
        ;

//        http.csrf().disable();
    }
}
