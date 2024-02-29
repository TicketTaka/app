package com.jspl.tickettaka.repository

import com.jspl.tickettaka.model.Admin
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepository: JpaRepository<Admin, Long> {
}