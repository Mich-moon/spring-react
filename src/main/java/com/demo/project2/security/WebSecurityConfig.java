// ... HttpSecurity configurations to configure cors, csrf, session management, rules for protected resources.

package com.demo.project2.security;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import com.demo.project2.security.services.UserDetailsServiceImpl;
import com.demo.project2.security.jwt.AuthEntryPointJwt;
import com.demo.project2.security.jwt.AuthTokenFilter;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        // securedEnabled = true,
        // jsr250Enabled = true,
        prePostEnabled = true)
public class WebSecurityConfig {

    // @EnableGlobalMethodSecurity provides AOP security on methods.
    // It enables @PreAuthorize, @PostAuthorize, it also supports JSR-250.

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public UserDetailsServiceImpl userDetailsService() {
        // ** WebSecurityConfigurerAdapter Deprecated

        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        // provide a PasswordEncoder for the DaoAuthenticationProvider by
        // the AuthenticationManagerBuilder.userDetailsService() method.

        return new BCryptPasswordEncoder();
    }

    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {

        // The implementation of UserDetailsService will be used for configuring DaoAuthenticationProvider by
        // AuthenticationManagerBuilder.userDetailsService() method.

        //authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        authenticationManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());

    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authConfig) throws Exception {
        // ** WebSecurityConfigurerAdapter Deprecated

        // AuthenticationManager has a DaoAuthenticationProvider (with help of UserDetailsService & PasswordEncoder)
        // to validate UsernamePasswordAuthenticationToken object. If successful, AuthenticationManager returns a
        // fully populated Authentication object (including granted authorities).

        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ** WebSecurityConfigurerAdapter Deprecated

        // Tell Spring Security how we configure CORS and CSRF,
        // which Exception Handler is chosen (AuthEntryPointJwt).
        // when we want to require all users to be authenticated or not,

        http
                .cors().and()
                .csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/users/**").permitAll()
                .antMatchers("/api/test/**").permitAll()
                .anyRequest().authenticated();


        // which filter (AuthTokenFilter)
        // and when we want it to work (filter before UsernamePasswordAuthenticationFilter),

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // ** WebSecurityConfigurerAdapter Deprecated

        return (web) -> web.ignoring().antMatchers("/images/**", "/js/**");
    }


    // To remove the warning “The type WebSecurityConfigurerAdapter is deprecated” in Spring-based application with Spring Security.
    // You need to declare SecurityFilterChain and WebSecurityCustomizer beans instead of overriding methods of WebSecurityConfigurerAdapter class.


}
