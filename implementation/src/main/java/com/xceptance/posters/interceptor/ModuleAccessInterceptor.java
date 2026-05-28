package com.xceptance.posters.interceptor;

import com.xceptance.posters.config.AdminUserPrincipal;
import com.xceptance.posters.config.BackofficeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Checks module-level access for backoffice requests.
 * If the user does not have access to the module matching the URL,
 * the request is forwarded to the access-denied page.
 */
@Component
public class ModuleAccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Only check /backoffice/** paths (skip login, logout, assets)
        if (!path.startsWith("/backoffice/") || path.startsWith("/backoffice/login") || path.startsWith("/backoffice/logout")) {
            return true;
        }

        // Dashboard is only accessible if user has explicit permission
        final BackofficeModule module = BackofficeModule.fromPath(path);
        if (module == null)
        {
            return true;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AdminUserPrincipal principal)) {
            return true; // Let Spring Security handle unauthenticated
        }

        if (!principal.hasModuleAccess(module)) {
            request.getRequestDispatcher("/backoffice/access-denied").forward(request, response);
            return false;
        }

        return true;
    }
}
