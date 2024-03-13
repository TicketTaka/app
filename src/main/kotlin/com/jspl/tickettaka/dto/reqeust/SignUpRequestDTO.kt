package com.jspl.tickettaka.dto.reqeust

data class SignUpRequestDTO(
    val email:String,
    val username:String,
    val password:String,
    val role:String
)
