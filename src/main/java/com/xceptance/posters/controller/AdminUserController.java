package com.xceptance.posters.controller;

import com.xceptance.posters.config.AdminUserPrincipal;
import com.xceptance.posters.entity.AdminUser;
import com.xceptance.posters.entity.RoleRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

/**
 * Controller for the Admin > Users submodule.
 */
@Controller
@RequestMapping("/backoffice/admin/users")
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
                       Model model) {
        Page<AdminUser> users = adminUserService.findAll(
                search.isBlank() ? null : search, roleId,
                PageRequest.of(page, size, Sort.by("username")));

        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("roleId", roleId);
        model.addAttribute("allRoles", roleRepository.findAll());
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
                         @RequestParam String password,
                         @RequestParam(name = "roleIds", required = false) Set<Long> roleIds,
                         RedirectAttributes redirectAttributes) {
        try {
            if (roleIds == null || roleIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "At least one role must be assigned");
                return "redirect:/backoffice/admin/users/new";
            }
            adminUserService.createUser(username, displayName, password, roleIds);
            redirectAttributes.addFlashAttribute("success", "User created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/backoffice/admin/users/new";
        }
        return "redirect:/backoffice/admin/users";
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
                         @RequestParam(name = "roleIds", required = false) Set<Long> roleIds,
                         @AuthenticationPrincipal AdminUserPrincipal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            if (roleIds == null || roleIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "At least one role must be assigned");
                return "redirect:/backoffice/admin/users/" + id + "/edit";
            }
            adminUserService.updateUser(id, username, displayName, roleIds, principal);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/backoffice/admin/users/" + id + "/edit";
        }
        return "redirect:/backoffice/admin/users";
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
        return "redirect:/backoffice/admin/users";
    }

    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable Long id,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/backoffice/admin/users/" + id + "/edit";
        }
        try {
            adminUserService.resetPassword(id, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password reset successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backoffice/admin/users/" + id + "/edit";
    }
}
