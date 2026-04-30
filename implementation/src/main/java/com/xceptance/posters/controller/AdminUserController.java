package com.xceptance.posters.controller;

import com.xceptance.posters.config.AdminUserPrincipal;
import com.xceptance.posters.entity.AdminUser;
import com.xceptance.posters.repository.RoleRepository;
import com.xceptance.posters.service.AdminUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

/**
 * Controller for the Admin > Users submodule.
 */
@Controller
@RequestMapping("/backoffice/security/users")
public class AdminUserController extends AbstractBackofficeController {

    private final AdminUserService adminUserService;
    private final RoleRepository roleRepository;

    public AdminUserController(AdminUserService adminUserService, RoleRepository roleRepository) {
        this.adminUserService = adminUserService;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String search,
                       @RequestParam(required = false) Long roleId,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size,
                       @AuthenticationPrincipal AdminUserPrincipal principal,
                       @RequestHeader(value = "HX-Request", required = false) String hxRequest,
                       Model model) {
        Page<AdminUser> users = adminUserService.findAll(
                search.isBlank() ? null : search, roleId,
                PageRequest.of(page, size, Sort.by("username")));

        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("roleId", roleId);
        model.addAttribute("currentUserId", principal.getUserId());
        model.addAttribute("allRoles", roleRepository.findAll());

        if ("true".equals(hxRequest)) {
            return "backoffice/admin/users/results";
        }
        return "backoffice/admin/users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("user", new AdminUser());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "backoffice/admin/users/form";
    }

    @PostMapping("/new")
    public String create(@RequestParam String username,
                         @RequestParam String displayName,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         @RequestParam(name = "roleId", required = false) Long roleId,
                         @AuthenticationPrincipal AdminUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "Passwords do not match");
                return "redirect:/backoffice/security/users/new";
            }
            if (roleId == null) {
                redirectAttributes.addFlashAttribute("error", "A role must be assigned");
                return "redirect:/backoffice/security/users/new";
            }
            adminUserService.createUser(username, displayName, email, password, Set.of(roleId), principal);
            redirectAttributes.addFlashAttribute("success", "User created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/backoffice/security/users/new";
        }
        return "redirect:/backoffice/security/users";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        AdminUser user = adminUserService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleRepository.findAll());
        return "backoffice/admin/users/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @RequestParam String username,
                         @RequestParam String displayName,
                         @RequestParam String email,
                         @RequestParam(name = "roleId", required = false) Long roleId,
                         @AuthenticationPrincipal AdminUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            if (roleId == null) {
                redirectAttributes.addFlashAttribute("error", "A role must be assigned");
                return "redirect:/backoffice/security/users/" + id + "/edit";
            }
            adminUserService.updateUser(id, username, displayName, email, Set.of(roleId), principal);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/backoffice/security/users/" + id + "/edit";
        }
        return "redirect:/backoffice/security/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal AdminUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            adminUserService.deleteUser(id, principal);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backoffice/security/users";
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                @AuthenticationPrincipal AdminUserPrincipal principal,
                                RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/backoffice/security/users/" + id + "/edit";
        }
        try {
            adminUserService.resetPassword(id, newPassword, principal);
            redirectAttributes.addFlashAttribute("success", "Password reset successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backoffice/security/users/" + id + "/edit";
    }
}
