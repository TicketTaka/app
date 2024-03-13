package com.jspl.tickettaka.model

import com.jspl.tickettaka.dto.response.CheckMemberResponse
import com.jspl.tickettaka.dto.response.MemberResponse
import com.jspl.tickettaka.model.enums.MemberRole
import jakarta.persistence.*

@Entity
@Table
class Member(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long? = null,

    @Column(name = "email")
    val email:String,

    @Column(name = "username")
    var username:String,

    @Column(name = "password")
    val password : String?,

    @Column(name = "role")
    var role : MemberRole
) {

}

fun Member.toResponse(): CheckMemberResponse {
    return  CheckMemberResponse(
        id = id!!,
        email = email,
        username = username,
        password = password,
        role = role.name
    )
}