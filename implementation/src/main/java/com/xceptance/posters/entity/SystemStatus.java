package com.xceptance.posters.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.persistence.Table;

/**
 * Key-value store for system-level state flags.
 * Used to persist readiness markers that survive restarts.
 */
@Entity
@Table(name = "system_status")
public class SystemStatus {

    @Id
    @Column(name = "status_key", nullable = false)
    private String key;

    @Version
    private Integer version;

    @Column(name = "status_value", nullable = false)
    private String value;

    public SystemStatus() {}

    public SystemStatus(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
