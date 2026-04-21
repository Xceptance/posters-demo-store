package com.xceptance.posters.config;
import com.xceptance.posters.entity.AuditLogEntry;

import com.xceptance.posters.entity.AuditLogEntry.Action;
import com.xceptance.posters.service.AuditLogService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Records LOGIN and LOGOUT actions to the audit log,
 * then delegates to Spring's default success/redirect behaviour.
 */
@Component
public class AuditLoginHandler
        implements AuthenticationSuccessHandler, LogoutSuccessHandler {

    private final AuditLogService auditLogService;

    // Spring's default handlers for post-log redirect
    private final SavedRequestAwareAuthenticationSuccessHandler loginDelegate =
            new SavedRequestAwareAuthenticationSuccessHandler();
    private final SimpleUrlLogoutSuccessHandler logoutDelegate =
            new SimpleUrlLogoutSuccessHandler();

    public AuditLoginHandler(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
        loginDelegate.setDefaultTargetUrl("/backoffice/");
        loginDelegate.setAlwaysUseDefaultTargetUrl(true);
        logoutDelegate.setDefaultTargetUrl("/backoffice/login?logout");
    }

    /** Called after successful form login. */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication.getPrincipal() instanceof AdminUserPrincipal principal) {
            auditLogService.log(
                    principal.getUserId(),
                    principal.getUsername(),
                    Action.LOGIN,
                    null, null,
                    "Login from " + request.getRemoteAddr()
            );
        }
        loginDelegate.onAuthenticationSuccess(request, response, authentication);
    }

    /** Called after logout. */
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.getPrincipal() instanceof AdminUserPrincipal principal) {
            auditLogService.log(
                    principal.getUserId(),
                    principal.getUsername(),
                    Action.LOGOUT,
                    null, null,
                    "Logout"
            );
        }
        logoutDelegate.onLogoutSuccess(request, response, authentication);
    }
}
