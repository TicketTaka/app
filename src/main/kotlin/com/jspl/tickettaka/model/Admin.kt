package com.jspl.tickettaka.model

import jakarta.persistence.*

@Entity
@Table(name = "admins")
class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val adminId: String? = null
}