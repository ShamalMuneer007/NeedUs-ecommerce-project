package com.needus.ecommerce.config;

import com.needus.ecommerce.entity.user.enums.Role;
import com.needus.ecommerce.service.security.CustomOidUserService;
import com.needus.ecommerce.service.security.UserInfoDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired
    UserInfoDetailsService userInfoDetailsService;
    @Autowired
    CustomOidUserService customOidUserService;
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomSuccessHandler();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
    @Bean
    public SessionRegistry sessionRegistry(){
        return new SessionRegistryImpl();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(
                auth ->
                    auth
                        .requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
                        .requestMatchers("/seller/**").hasAuthority(Role.SELLER.name())
                        .requestMatchers("/user/**").hasAnyAuthority(Role.USER.name(),Role.ADMIN.name(),Role.SELLER.name(),"OIDC_USER")
                        .requestMatchers("/**","/login","/signup","/register","/activation","/shop/home/**").permitAll()
                        .anyRequest().authenticated()
            )
            .formLogin(
                form ->
                    form
                        .loginPage("/login").permitAll()
                        .successHandler(authenticationSuccessHandler())
            )
            .sessionManagement(
                httpSecuritySessionManagementConfigurer ->
                    httpSecuritySessionManagementConfigurer
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                        .sessionRegistry(sessionRegistry())
                        .expiredUrl("/login?sessionExpired")
            )
            .oauth2Login(
                oauth ->
                    oauth
                        .loginPage("/login")
                        .successHandler(authenticationSuccessHandler())
                        .userInfoEndpoint().userService(customOidUserService)
            )
            .logout(logout ->
                    logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
            )

            .cors().and().csrf().disable();
        return http.build();
    }
    @Bean
    public WebSecurityCustomizer securityCustomizer(){
        return (web) -> web.ignoring().requestMatchers("/img/**","/styles/**","/uploads/**");
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
