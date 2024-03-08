package com.jspl.tickettaka.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.net.URI

@Entity
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val message: String,
    val notificationType: String,
    val relatedUri: URI,
//    val members: List<Member>
)