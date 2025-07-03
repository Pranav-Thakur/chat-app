package com.chatapp.dao.repository;

import com.chatapp.dao.constants.AppUserStatus;
import com.chatapp.dao.model.AppUserDO;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUserDO, UUID> {
    boolean existsByUserIdAndStatus(@NonNull UUID userId, @NonNull AppUserStatus status);
    Optional<AppUserDO> findByPhone(@NonNull String phone);
}
