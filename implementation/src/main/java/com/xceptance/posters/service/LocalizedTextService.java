package com.xceptance.posters.service;
import com.xceptance.posters.entity.LocalizedText;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service to resolve localized text IDs to actual text strings.
 * Used by the storefront to get category names, product names/descriptions
 * in the user's locale.
 */
@Service
@Transactional(readOnly = true)
public class LocalizedTextService {

    @PersistenceContext
    private EntityManager em;

    /**
     * Returns the text for the given textId and locale code.
     * Falls back to en-US if the specific locale is not found.
     * Returns "???" if neither is found.
     */
    public String getText(Integer textId, String localeCode) {
        if (textId == null) return "";

        // Try exact locale first
        List<LocalizedText> results = em.createQuery(
            "SELECT lt FROM LocalizedText lt WHERE lt.textId = :textId AND lt.locale.locale = :code",
            LocalizedText.class)
            .setParameter("textId", textId)
            .setParameter("code", localeCode)
            .getResultList();

        if (!results.isEmpty()) {
            return results.get(0).getText();
        }

        // Fallback to en-US
        if (!"en-US".equals(localeCode)) {
            results = em.createQuery(
                "SELECT lt FROM LocalizedText lt WHERE lt.textId = :textId AND lt.locale.locale = :code",
                LocalizedText.class)
                .setParameter("textId", textId)
                .setParameter("code", "en-US")
                .getResultList();

            if (!results.isEmpty()) {
                return results.get(0).getText();
            }
        }

        // Last fallback: any locale
        results = em.createQuery(
            "SELECT lt FROM LocalizedText lt WHERE lt.textId = :textId",
            LocalizedText.class)
            .setParameter("textId", textId)
            .setMaxResults(1)
            .getResultList();

        return results.isEmpty() ? "???" : results.get(0).getText();
    }

    /**
     * Bulk-loads all localized texts for the given locale codes in a single query.
     * Returns a nested map: textId → localeCode → text.
     *
     * <p>This avoids the N+1 query problem when indexing many products across
     * multiple languages (e.g. Lucene index build on startup).</p>
     *
     * @param localeCodes the locale codes to load texts for (e.g. "en-US", "de-DE")
     * @return map from textId to (localeCode → text)
     */
    public Map<Integer, Map<String, String>> getAllTextsForLocales(final Collection<String> localeCodes)
    {
        final List<LocalizedText> allTexts = em.createQuery(
            "SELECT lt FROM LocalizedText lt JOIN FETCH lt.locale WHERE lt.locale.locale IN :codes",
            LocalizedText.class)
            .setParameter("codes", localeCodes)
            .getResultList();

        final Map<Integer, Map<String, String>> result = new HashMap<>();
        for (final LocalizedText lt : allTexts)
        {
            result
                .computeIfAbsent(lt.getTextId(), k -> new HashMap<>())
                .put(lt.getLocale().getLocale(), lt.getText());
        }
        return result;
    }
}
