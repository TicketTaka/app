package com.jspl.tickettaka.notification

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import org.springframework.data.annotation.Id
import java.net.URI

class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val message: String,
    val notificationType: String,
    val relatedUri: URI,
//    val members: List<Member>
)