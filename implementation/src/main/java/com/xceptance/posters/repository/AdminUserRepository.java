package com.xceptance.posters.repository;
import com.xceptance.posters.entity.Role;
import com.xceptance.posters.entity.AdminUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Spring Data repository for admin users.
 */
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByUsername(String username);

    @Query("SELECT u FROM AdminUser u WHERE LOWER(u.username) LIKE :search OR LOWER(u.displayName) LIKE :search OR LOWER(u.email) LIKE :search")
    Page<AdminUser> findBySearch(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(u) FROM AdminUser u JOIN u.roles r WHERE r = :role")
    long countByRolesContaining(@Param("role") Role role);

    Page<AdminUser> findByRolesId(Long roleId, Pageable pageable);
}
