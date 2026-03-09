package com.xceptance.posters.entity;

import java.io.Serializable;
import java.util.Objects;

public class LocalizedTextId implements Serializable {

    private Integer textId;
    private Integer locale;

    public LocalizedTextId() {
    }

    public LocalizedTextId(Integer textId, Integer locale) {
        this.textId = textId;
        this.locale = locale;
    }

    public Integer getTextId() {
        return textId;
    }

    public void setTextId(Integer textId) {
        this.textId = textId;
    }

    public Integer getLocale() {
        return locale;
    }

    public void setLocale(Integer locale) {
        this.locale = locale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalizedTextId that = (LocalizedTextId) o;
        return Objects.equals(textId, that.textId) && Objects.equals(locale, that.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(textId, locale);
    }
}
