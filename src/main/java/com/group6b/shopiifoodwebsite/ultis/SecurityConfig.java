package com.group6b.shopiifoodwebsite.ultis;

import com.group6b.shopiifoodwebsite.services.OAuthService;
import com.group6b.shopiifoodwebsite.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private final UserService userService;
    @Autowired
    private final OAuthService oAuthService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/", "/oauth/**", "/register",
                                "/error", "/fonts/**", "/vendor/**", "/images/**", "/auth/**", "/foodimages/**", "/restaurantpictures/**",
                                "/foods/details/**", "/categories/details/**", "/restaurants/details/**").permitAll()
                        .requestMatchers("/foods/edit/**", "/foods/add", "/foods/delete").hasAnyAuthority("ADMIN", "SELLER")
                        .requestMatchers("/categories/edit/**", "/categories/add", "/categories/delete").hasAnyAuthority("ADMIN")
                        .requestMatchers("/restaurants/edit/**", "/restaurants/add", "/restaurants/delete").hasAnyAuthority("ADMIN")
                        .requestMatchers("/cart", "/cart/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/orders/", "/orders/**").hasAnyAuthority("ADMIN", "USER")
                        .requestMatchers("/sellerDashboard/**", "/restaurants/edit/**", "/restaurants/add", "/restaurants/delete").hasAnyAuthority("ADMIN", "SELLER")
                        .anyRequest().authenticated())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/login?error")
                        .permitAll())
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userService(oAuthService))
                        .successHandler((request, response, authentication) -> {
                            var oidcUser = (DefaultOidcUser) authentication.getPrincipal();
                            userService.saveOauthUser(oidcUser.getEmail(), oidcUser.getName());
                            response.sendRedirect("/");
                        })
                        .permitAll())

                .rememberMe(rememberMe -> rememberMe
                        .key("hutech")
                        .rememberMeCookieName("hutech")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userDetailsService()))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/403"))
                .sessionManagement(sessionManagement -> sessionManagement
                        .maximumSessions(1)
                        .expiredUrl("/login"))
                .httpBasic(httpBasic -> httpBasic.realmName("hutech"))
                .build();
    }

}
