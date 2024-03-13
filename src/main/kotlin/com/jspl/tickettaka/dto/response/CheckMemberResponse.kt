package com.jspl.tickettaka.dto.response

data class CheckMemberResponse(
    val id :Long?,
    val email :String,
    val username:String,
    val password :String?,
    val role:String
)
