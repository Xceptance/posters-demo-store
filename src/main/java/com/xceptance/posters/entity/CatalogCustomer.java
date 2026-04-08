package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Represents a customer account in the storefront.
 *
 * <p>New accounts start in {@link AccountStatus#PENDING} state and
 * transition to {@link AccountStatus#ACTIVE} once the email verification
 * link is clicked.
 */
@Entity(name = "CatalogCustomer")
@Table(name = "customers")
public class CatalogCustomer {

    /**
     * Account lifecycle states.
     */
    public enum AccountStatus
    {
        /** Email not yet verified. */
        PENDING,
        /** Email verified, fully active. */
        ACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus = AccountStatus.PENDING;

    @Column(name = "verification_token", unique = true)
    private String verificationToken;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "catalog_customer_credit_cards",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "credit_card_id")
    )
    private Set<CatalogCreditCard> creditCards = new HashSet<>();

    @PrePersist
    private void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<CatalogCreditCard> getCreditCards() {
        return creditCards;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Hashes the given plain-text password and sets it.
     */
    public void hashPassword(String plainPassword) {
        setPassword(BCrypt.hashpw(plainPassword, BCrypt.gensalt()));
    }

    /**
     * Checks whether the given plain-text password matches the stored hash.
     */
    public boolean checkPassword(String plainPassword) {
        return this.password != null && BCrypt.checkpw(plainPassword, this.password);
    }

    // --- Account status ---

    public AccountStatus getAccountStatus()
    {
        return accountStatus;
    }

    public void setAccountStatus(final AccountStatus accountStatus)
    {
        this.accountStatus = accountStatus;
    }

    /**
     * Returns {@code true} if the account has not yet been verified.
     */
    public boolean isPending()
    {
        return accountStatus == AccountStatus.PENDING;
    }

    /**
     * Transitions this account to {@link AccountStatus#ACTIVE} and
     * clears the verification token fields.
     */
    public void activate()
    {
        this.accountStatus = AccountStatus.ACTIVE;
        this.verificationToken = null;
        this.tokenExpiresAt = null;
    }

    // --- Verification token ---

    public String getVerificationToken()
    {
        return verificationToken;
    }

    public void setVerificationToken(final String verificationToken)
    {
        this.verificationToken = verificationToken;
    }

    public LocalDateTime getTokenExpiresAt()
    {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(final LocalDateTime tokenExpiresAt)
    {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    /**
     * Returns {@code true} if the verification token has expired.
     */
    public boolean isTokenExpired()
    {
        return tokenExpiresAt != null && LocalDateTime.now().isAfter(tokenExpiresAt);
    }
}
