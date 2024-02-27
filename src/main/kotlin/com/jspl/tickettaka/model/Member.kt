package com.jspl.tickettaka.model

import com.jspl.tickettaka.dto.response.MemberResponse
import com.jspl.tickettaka.model.enums.MemberRole
import jakarta.persistence.*

@Entity
@Table
class Member(

    @Column(name = "email")
    val email:String,

    @Column(name = "username")
    val username:String,

    @Column(name = "password")
    val password : String,

    @Column(name = "role")
    val role : MemberRole
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null
}

fun Member.toResponse(): MemberResponse {
    return  MemberResponse(
        email = email,
        username = username,
        role = role.name
    )
}