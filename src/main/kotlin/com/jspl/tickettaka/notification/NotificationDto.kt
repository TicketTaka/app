package com.jspl.tickettaka.notification

import java.net.URI

data class NotificationDto(
    val message: String,
    val notificationType: String,
    val relatedUri: URI
) {
    companion object {
        fun fromEntity(notification: Notification): NotificationDto {
            return NotificationDto(
                message = notification.message,
                notificationType = notification.notificationType,
                relatedUri = notification.relatedUri
            )
        }
    }
}