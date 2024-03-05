package com.jspl.tickettaka.notification

import java.net.URI

data class NotificationEvent(
    val memberKey: String,
    val message: String,
    val notificationType: String,
    val relatedUri: URI
)