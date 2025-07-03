package com.chatapp.dao.model;

import com.chatapp.dao.constants.LoginUserStatus;
import com.chatapp.dao.service.MapToJsonConverter;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Data
@Table(name = "login_user", indexes = {
    @Index(name = "idx_login_user_phone", columnList = "phone")
})
public class LoginUserDO extends AuditableBaseDO {
    @Id
    @GeneratedValue
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false, columnDefinition = "BINARY(16)")
    private UUID sessionId;

    @Column(nullable = false)
    private String phone; // 10 digit

    @Column(nullable = false)
    private String did;

    private String agent;
    private String otp;

    @Lob
    @Convert(converter = MapToJsonConverter.class)
    private Map<String, Object> info = new HashMap<>();

    @Enumerated(EnumType.STRING)
    private LoginUserStatus status;
}
