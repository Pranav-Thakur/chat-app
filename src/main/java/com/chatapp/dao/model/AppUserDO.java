package com.chatapp.dao.model;

import com.chatapp.dao.constants.AppUserStatus;
import com.chatapp.dao.service.MapToJsonConverter;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Table(name = "chatapp_user", indexes = {
        @Index(name = "idx_chatapp_user_phone", columnList = "phone"),
        @Index(name = "idx_chatapp_user_email", columnList = "email")
})
public class AppUserDO extends AuditableBaseDO {
    @Id
    @GeneratedValue
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String phone; // 10 digit

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;
    private String about;

    @Lob
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> info = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private AppUserStatus status;
}
