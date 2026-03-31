package com.xceptance.posters.controller;

import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomerRepository;
import com.xceptance.posters.service.SessionService;

import jakarta.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for registration validation logic in CustomerController.
 * Uses Mockito to isolate the controller from its dependencies.
 */
@ExtendWith(MockitoExtension.class)
class CustomerControllerUnitTest
{
    @Mock
    private CatalogCustomerRepository customerRepository;

    @Mock
    private SessionService sessionService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private CustomerController controller;

    private static final String LOCALE = "en_US";
    private static final String VALID_PASSWORD = "Abcdefgh1!";
    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_FIRST_NAME = "John";
    private static final String VALID_LAST_NAME = "Doe";

    @BeforeEach
    void setUp()
    {
        // Default: messageSource returns a generic message for any key
        lenient().when(messageSource.getMessage(anyString(), any(), any(Locale.class)))
            .thenReturn("error message");
    }

    // --- Empty field tests ---

    @Test
    void registerRejectsEmptyFirstName()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
            "", VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(redirectAttributes).addFlashAttribute(eq("error"), anyString());
        verify(messageSource).getMessage(eq("errorFieldsRequired"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerRejectsEmptyLastName()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
            VALID_FIRST_NAME, "", session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorFieldsRequired"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerRejectsEmptyEmail()
    {
        String result = controller.register(LOCALE, "", VALID_PASSWORD, VALID_PASSWORD,
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorFieldsRequired"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerRejectsEmptyPassword()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, "", "",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorFieldsRequired"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerRejectsEmptyConfirmPassword()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, VALID_PASSWORD, "",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorFieldsRequired"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerRejectsWhitespaceOnlyFields()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
            "   ", VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorFieldsRequired"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    // --- Email format tests ---

    @ParameterizedTest
    @ValueSource(strings = {
        "noatsign",             // no @
        "missing@dot",          // no dot after @
        "@nodomain.com",        // no local part
        "user@.com",            // dot immediately after @
        "user@domain.",         // dot at end with nothing after
        "user name@domain.com"  // space in email
    })
    void registerRejectsInvalidEmailFormat(String invalidEmail)
    {
        String result = controller.register(LOCALE, invalidEmail, VALID_PASSWORD, VALID_PASSWORD,
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorValidEmail"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    // --- Password strength tests ---

    @Test
    void registerRejectsPasswordTooShort()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, "Ab1!", "Ab1!",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorPasswordTooWeak"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    @Test
    void registerRejectsPasswordNoUppercase()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, "abcdefgh1!", "abcdefgh1!",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorPasswordTooWeak"), any(), any(Locale.class));
    }

    @Test
    void registerRejectsPasswordNoLowercase()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, "ABCDEFGH1!", "ABCDEFGH1!",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorPasswordTooWeak"), any(), any(Locale.class));
    }

    @Test
    void registerRejectsPasswordNoDigit()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, "Abcdefghij!", "Abcdefghij!",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorPasswordTooWeak"), any(), any(Locale.class));
    }

    @Test
    void registerRejectsPasswordNoSpecialChar()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, "Abcdefgh12", "Abcdefgh12",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorPasswordTooWeak"), any(), any(Locale.class));
    }

    @Test
    void registerRejectsPasswordWithSpaces()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, "Abcde fgh1!", "Abcde fgh1!",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorPasswordTooWeak"), any(), any(Locale.class));
    }

    // --- Password mismatch ---

    @Test
    void registerRejectsPasswordMismatch()
    {
        String result = controller.register(LOCALE, VALID_EMAIL, VALID_PASSWORD, "DifferentP1!",
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(messageSource).getMessage(eq("errorPasswordMatch"), any(), any(Locale.class));
        verify(customerRepository, never()).save(any());
    }

    // --- Duplicate email ---

    @Test
    void registerRejectsDuplicateEmail()
    {
        when(customerRepository.existsByEmail(VALID_EMAIL)).thenReturn(true);

        String result = controller.register(LOCALE, VALID_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/register");
        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Email already in use."));
        verify(customerRepository, never()).save(any());
    }

    // --- Successful registration ---

    @Test
    void registerSucceedsWithValidInput()
    {
        when(customerRepository.existsByEmail(VALID_EMAIL)).thenReturn(false);
        CatalogCustomer savedCustomer = new CatalogCustomer();
        savedCustomer.setEmail(VALID_EMAIL);
        savedCustomer.setFirstName(VALID_FIRST_NAME);
        savedCustomer.setLastName(VALID_LAST_NAME);
        when(customerRepository.save(any(CatalogCustomer.class))).thenReturn(savedCustomer);

        String result = controller.register(LOCALE, VALID_EMAIL, VALID_PASSWORD, VALID_PASSWORD,
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/");
        verify(customerRepository).save(any(CatalogCustomer.class));
        verify(sessionService).setCustomerId(eq(session), any());
    }

    // --- Parameterized: valid passwords that should pass ---

    @ParameterizedTest
    @ValueSource(strings = {
        "Abcdefgh1!",       // exact minimum: 10 chars
        "MyP@ssw0rd!",      // typical strong password
        "C0mpl3x!Pass",     // 12 chars
        "X#9aaaaaaaaa"      // special char near start
    })
    void registerAcceptsStrongPasswords(String password)
    {
        when(customerRepository.existsByEmail(VALID_EMAIL)).thenReturn(false);
        CatalogCustomer savedCustomer = new CatalogCustomer();
        when(customerRepository.save(any(CatalogCustomer.class))).thenReturn(savedCustomer);

        String result = controller.register(LOCALE, VALID_EMAIL, password, password,
            VALID_FIRST_NAME, VALID_LAST_NAME, session, redirectAttributes);

        assertThat(result).isEqualTo("redirect:/" + LOCALE + "/");
    }
}
