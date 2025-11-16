package com.nhattung.wogo.repository;

import com.nhattung.wogo.entity.Notification;
import com.nhattung.wogo.enums.ROLE;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByTargetRoleOrderByCreatedAtDesc(ROLE role);
}
