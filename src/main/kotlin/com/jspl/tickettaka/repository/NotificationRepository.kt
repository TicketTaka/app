package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Notification
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository : JpaRepository<Notification, Long> {
}