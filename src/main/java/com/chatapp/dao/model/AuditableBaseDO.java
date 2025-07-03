package com.chatapp.dao.model;

import lombok.Data;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public abstract class AuditableBaseDO {

    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @Version
    private int version;

    @PrePersist
    protected void onCreate() {
        this.version = 0;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
