package com.jspl.tickettaka.notification

import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class NotificationEventHandler(private val notificationService: NotificationService) {
    @Async
    @EventListener // 이벤트 구독
    fun handleEvent(event: NotificationEvent) {
        notificationService.sendNotification(event)
    }
}