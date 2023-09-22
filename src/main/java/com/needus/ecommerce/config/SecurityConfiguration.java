package com.needus.ecommerce.config;

import com.needus.ecommerce.entity.Role;
import com.needus.ecommerce.service.UserInfoDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    UserInfoDetailsService userInfoDetailsService;
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSuccessHandler();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                auth ->
                    auth
                        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/seller/**").hasAuthority(Role.USER.name())
                        .requestMatchers("/login","/signup","/register","/activation").permitAll()
                        .anyRequest().authenticated()
            )
            .formLogin(
                form ->
                    form
                        .loginPage("/login").permitAll()
                        .successHandler(authenticationSuccessHandler())
            )
            .oauth2Login(
                oauth ->
                    oauth
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
            )
            .logout(logout ->
                    logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
            );
        return http.build();
    }
    @Bean
    public WebSecurityCustomizer securityCustomizer(){
        return (web) -> web.ignoring().requestMatchers("/img/**","/styles/**");
    }
    @Bean
    public AuthenticationProvider daoAuthenticationProvide(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder());
        authenticationProvider.setUserDetailsService(userInfoDetailsService);
        return authenticationProvider;
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
