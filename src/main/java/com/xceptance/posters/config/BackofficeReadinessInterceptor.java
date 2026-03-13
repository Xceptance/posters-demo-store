package com.xceptance.posters.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Intercepts all backoffice requests while the app is still initializing (seeding).
 * Redirects to /backoffice/starting until BackofficeReadinessService reports ready.
 */
@Component
public class BackofficeReadinessInterceptor implements HandlerInterceptor {

    private static final String STARTING_PATH = "/backoffice/starting";

    private final BackofficeReadinessService readinessService;

    public BackofficeReadinessInterceptor(BackofficeReadinessService readinessService) {
        this.readinessService = readinessService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (readinessService.isReady()) {
            return true;
        }

        String uri = request.getRequestURI();

        // Always allow the starting page itself and static assets through
        if (uri.startsWith(STARTING_PATH) || uri.startsWith("/assets/")) {
            return true;
        }

        response.sendRedirect(STARTING_PATH);
        return false;
    }
}
