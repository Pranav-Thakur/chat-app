package com.chatapp.dao.repository;

import com.chatapp.dao.constants.LoginUserStatus;
import com.chatapp.dao.model.LoginUserDO;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoginUserRepository extends JpaRepository<LoginUserDO, UUID> {
    boolean existsBySessionIdAndStatus(@NonNull UUID sessionId, @NonNull LoginUserStatus status);

    @Query(value = "SELECT u FROM LoginUserDO u WHERE u.phone = :phone AND u.status = :status ORDER BY u.createdDate DESC")
    Optional<List<LoginUserDO>> findByPhoneAndStatus(@NonNull @Param("phone") String phone, @NonNull  @Param("status") LoginUserStatus status, Pageable lmt);
}
