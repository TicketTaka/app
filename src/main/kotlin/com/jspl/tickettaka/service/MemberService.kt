package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.LoginRequestDTO
import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.dto.response.AccessTokenResponse
import com.jspl.tickettaka.dto.response.CheckMemberResponse
import com.jspl.tickettaka.infra.jwt.JwtPlugin
import com.jspl.tickettaka.model.Member
import com.jspl.tickettaka.model.enums.MemberRole
import com.jspl.tickettaka.model.toResponse
import com.jspl.tickettaka.repository.MemberRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin
    ) {

    val kakaoClientId = "60ffdbd138489440034b2e2bb1f592e3"
    val kakaoRedirectUri = "http://localhost:8080/api/members/getKakaoAccessToken"

    fun signUp(request: SignUpRequestDTO): CheckMemberResponse {
        val (email) = request

        //이메일 중복확인
        if (findByEmail(email) != null) {
            throw IllegalArgumentException("이미 존재하는 email 입니다")
        }

        //유저 db에 저장
        val signUpMember = saveMember(request)

        //트랜잭션 내부라면 작성하지 않아도 되는 로직
        val saveMember = memberRepository.save(signUpMember)
        val MappingMember = saveMember.toResponse()
        return MappingMember
    }

    fun login(request:LoginRequestDTO):AccessTokenResponse{
        val (email, password) = request
        val foundMember = findByEmail(email)

        if (foundMember == null && passwordEncoder.matches(password,foundMember)) {
            throw IllegalArgumentException("Email 또는 Password 가 잘못 입력되었습니다")
        }

        val data = jwtPlugin.generateAccessToken(
            subject = foundMember!!.id.toString(),
            username = foundMember.username,
            role = foundMember.role.name
        )
        return AccessTokenResponse(data)
    }

    fun getKakaoAccessToken(code :String):AccessTokenResponse{
        val restTemplate = RestTemplate()
        val params: MultiValueMap<String, String> = LinkedMultiValueMap()
        params.add("grant_type", "authorization_code")
        params.add("client_id", kakaoClientId)
        params.add("redirect_uri", kakaoRedirectUri)
        params.add("code", code)


        val responseToken = restTemplate.postForObject(
            "https://kauth.kakao.com/oauth/token",
            params,
            Map::class.java
        )!!

        val access_token = responseToken["access_token"]


        // 사용자 정보 가져오기 요청
        var userInfo = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET,
            HttpEntity(null, HttpHeaders().apply { set("Authorization", "Bearer $access_token") }),
            Map::class.java
        ).body!!

        var userNickname =  userInfo["properties"].toString()

        userNickname = userNickname
            .replace("{","")
            .replace("}","")
            .split("=").get(1)

        var kakaoId = userInfo["id"].toString()

        kakaoId = kakaoId
            .replace("{","")
            .replace("}","")
            .split("=").get(0)

        val foundMember = saveKakaoMember(kakaoId,userNickname)

        val data = jwtPlugin.generateAccessToken(
            subject = foundMember.id.toString(),
            username = userNickname,
            role = foundMember.role
        )

        return AccessTokenResponse(data)
    }

    fun memberRoleChange(member :User):String  {
         val memberInfo = findByMemberId(member)

        memberInfo.role =  when (memberInfo.role.name) {
            "TempNameProducer" -> MemberRole.TempNameConsumer
            "TempNameConsumer" -> MemberRole.TempNameProducer
            else -> throw IllegalArgumentException("잘못된 접근입니다")
        }

        memberRepository.save(memberInfo)
        return "유저의 정보가 ${memberInfo.role.name}로 변환되었습니다"
    }


    //임시 데이터 뷰
    fun viewAllMemberData():List<CheckMemberResponse> {
        return  memberRepository.findAll().map { it.toResponse() }
    }

    private fun saveMember(request:SignUpRequestDTO):Member{
        val (email, username, password, role) = request
        val emailRegularExpression = "^[a-zA-Z0-9]+@[a-zA-Z0-9-]+.com$"

        if(!emailRegularExpression.toRegex().matches(email)){
            throw IllegalArgumentException("이메일 형식이 맞지 않습니다")
        }

        val signUpMember = Member(
            email = email,
            username = username,
            password = passwordEncoder.encode(password),
            role = when (role) {
                "TempNameProducer" -> MemberRole.TempNameProducer
                "TempNameConsumer" -> MemberRole.TempNameConsumer
                else -> throw IllegalArgumentException("잘못된 접근입니다")
            }
        )

        return signUpMember
    }
    private fun saveKakaoMember(kakaoId :String,userNickname:String) :CheckMemberResponse{
        val userData = Member(
            email = kakaoId,
            username = userNickname,
            password = null,
            role = MemberRole.TempNameConsumer
        )


       return  memberRepository.save(userData).toResponse()
    }

    private fun findByEmail(email: String): Member? {
        return memberRepository.findByEmail(email)
    }

    private fun findByMemberId(member: User):Member {
        //추후 null 생각하기
        return memberRepository.findByIdOrNull(member.username.toLong())!!
    }



}