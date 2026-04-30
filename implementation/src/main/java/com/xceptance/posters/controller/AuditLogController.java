package com.xceptance.posters.controller;

import com.xceptance.posters.entity.AuditLogEntry;
import com.xceptance.posters.service.AuditLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the Admin > Audit Log submodule.
 */
@Controller
@RequestMapping("/backoffice/security/audit-log")
public class AuditLogController extends AbstractBackofficeController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String username,
                       @RequestParam(required = false) String action,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "25") int size,
                       Model model) {
        AuditLogEntry.Action actionFilter = null;
        if (action != null && !action.isBlank()) {
            try {
                actionFilter = AuditLogEntry.Action.valueOf(action);
            } catch (IllegalArgumentException ignored) {}
        }

        var entries = auditLogService.search(
                username.isBlank() ? null : username,
                actionFilter,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")));

        model.addAttribute("entries", entries);
        model.addAttribute("username", username);
        model.addAttribute("action", action);
        model.addAttribute("actions", AuditLogEntry.Action.values());
        return "backoffice/admin/audit-log/list";
    }
}
