package com.jspl.tickettaka.notification

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class NotificationEventPublisher(private val eventPublisher: ApplicationEventPublisher) {
    fun publishEvent(event: NotificationEvent) {
        eventPublisher.publishEvent(event) // publishEvent()로 이벤트를 발행
    }
}
