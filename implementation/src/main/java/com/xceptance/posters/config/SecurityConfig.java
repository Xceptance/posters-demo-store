package com.xceptance.posters.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security configuration with two isolated filter chains:
 * 1) Backoffice (/backoffice/**) — requires admin authentication
 * 2) Storefront (/**) — permits all, no authentication
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuditLoginHandler auditLoginHandler;

    public SecurityConfig(AuditLoginHandler auditLoginHandler) {
        this.auditLoginHandler = auditLoginHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Backoffice security — form login, session-based auth.
     * Higher priority (lower order number) so it matches /backoffice/** first.
     */
    @Bean
    @Order(1)
    public SecurityFilterChain backofficeFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/backoffice/**", "/error")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/backoffice/login", "/backoffice/starting", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/backoffice/login")
                .loginProcessingUrl("/backoffice/login")
                .successHandler(auditLoginHandler)   // records LOGIN + redirects
                .failureUrl("/backoffice/login?error")
                .usernameParameter("username")
                .passwordParameter("password")
            )
            .logout(logout -> logout
                .logoutUrl("/backoffice/logout")
                .logoutSuccessHandler(auditLoginHandler)  // records LOGOUT + redirects
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/backoffice/access-denied")
            );

        return http.build();
    }

    /**
     * Storefront security — permit everything, CSRF protection enabled.
     * Excludes stateless API endpoints (/api/v2/**) from CSRF validation.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain storefrontFilterChain(final HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/v2/**")
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler(csrfAccessDeniedHandler())
            );

        return http.build();
    }

    /**
     * Custom access denied handler for CSRF validation failures.
     * Provides user-friendly error messages for session timeouts.
     */
    private AccessDeniedHandler csrfAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            if (accessDeniedException instanceof MissingCsrfTokenException ||
                accessDeniedException instanceof InvalidCsrfTokenException) {
                response.sendRedirect(request.getContextPath() + "/error?reason=session-expired");
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        };
    }
}
