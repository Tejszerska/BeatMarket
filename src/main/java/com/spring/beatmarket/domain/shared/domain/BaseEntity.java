package com.spring.beatmarket.domain.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    protected UUID uuid = UUID.randomUUID();
    @CreationTimestamp
    protected Instant createdOn;
    @UpdateTimestamp
    protected Instant editedOn;
    @Column(nullable = false)
    protected boolean active = true;

    @Version
    protected Long version;

    public void deactivate(){
        this.active = false;
    }

    public boolean isActive() {return this.active;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }


}
