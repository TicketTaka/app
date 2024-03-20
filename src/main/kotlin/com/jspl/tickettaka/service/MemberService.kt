package com.jspl.tickettaka.service

import com.jspl.tickettaka.dto.reqeust.LoginRequestDTO
import com.jspl.tickettaka.dto.reqeust.SignUpRequestDTO
import com.jspl.tickettaka.dto.response.AccessTokenResponse
import com.jspl.tickettaka.dto.response.CheckMemberResponse
import com.jspl.tickettaka.dto.response.TicketResponse
import com.jspl.tickettaka.infra.exception.ApiResponseCode
import com.jspl.tickettaka.infra.exception.ErrorResponse
import com.jspl.tickettaka.infra.exception.ModelNotFoundException
import com.jspl.tickettaka.infra.jwt.JwtPlugin
import com.jspl.tickettaka.model.Member
import com.jspl.tickettaka.model.Ticket
import com.jspl.tickettaka.model.enums.MemberRole
import com.jspl.tickettaka.model.toResponse
import com.jspl.tickettaka.repository.MemberRepository
import com.jspl.tickettaka.repository.TicketRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
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
import kotlin.jvm.Throws

@Service
@Transactional
class MemberService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
    private val ticketRepository: TicketRepository,
    private val ticketService: TicketService,
    private val jwtPlugin: JwtPlugin,

    @Value("\${kakao.secret.kakaoClientId}")
    private val kakaoClientId: String,

    @Value("\${kakao.secret.kakaoRedirectUri}")
    private val kakaoRedirectUri: String

) {

//    val kakaoClientId = "60ffdbd138489440034b2e2bb1f592e3"
//    val kakaoRedirectUri = "http://localhost:8080/api/members/getKakaoAccessToken"

    fun signUp(signUpRequestDTO: SignUpRequestDTO): CheckMemberResponse {
        val (email) = signUpRequestDTO

        //이메일 중복확인
        if (findByEmail(email) != null) {
            throw IllegalArgumentException("이미 존재하는 email 입니다")
//            throw ErrorResponse(null,"이미 존재하는 email 입니다")
//            ErrorResponse(ApiResponseCode.NOT_ACCEPTABLE,"이미 존재하는 email 입니다")
//            ApiResponseCode.NOT_ACCEPTABLE
//            ErrorResponse(ApiResponseCode.NOT_ACCEPTABLE, "이미 존재하는 email 입니다")
//            val data = ErrorResponse(ApiResponseCode.BAD_REQUEST,"이미존재하는 email")

        }

        //유저 db에 저장
        val signUpMember = saveMember(signUpRequestDTO)

        val saveMember = memberRepository.save(signUpMember)
        val mappingMember = saveMember.toResponse()
        return mappingMember
    }

    fun login(request: LoginRequestDTO): AccessTokenResponse {
        val (email, password) = request
        val foundMember = findByEmail(email)

        if (foundMember == null && passwordEncoder.matches(password, foundMember)) {
            throw IllegalArgumentException("Email 또는 Password 가 잘못 입력되었습니다")
        }

        val data = jwtPlugin.generateAccessToken(
            subject = foundMember!!.id.toString(),
            username = foundMember.username,
            role = foundMember.role.name
        )
        return AccessTokenResponse(data)
    }

    fun getKakaoAccessToken(code: String): AccessTokenResponse {
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
        val userInfo = restTemplate.exchange(
            "https://kapi.kakao.com/v2/user/me",//String url
            HttpMethod.GET,                         //HttpMethod method,
            HttpEntity(
                null,
                HttpHeaders().apply {
                    set(
                        "Authorization",
                        "Bearer $access_token"
                    )
                }),  //@Nullable HttpEntity<?> requestEntity
            Map::class.java  //Class<T> responseType
        ).body!!

        var userNickname = userInfo["properties"].toString()

        userNickname = userNickname
            .replace("{", "")
            .replace("}", "")
            .split("=").get(1)

        var kakaoId = userInfo["id"].toString()

        kakaoId = kakaoId
            .replace("{", "")
            .replace("}", "")
            .split("=").get(0)

        val foundMember = saveKakaoMember(kakaoId, userNickname)

        val data = jwtPlugin.generateAccessToken(
            subject = foundMember.id.toString(),
            username = userNickname,
            role = foundMember.role
        )

        return AccessTokenResponse(data)
    }

    //회원삭제
    fun deleteMember(memberId: Long) {
        val memberInfo = findByMemberId(memberId)
        val ticketInfo = findTicketInfoByMemberId(memberId)

        if(ticketInfo.isNotEmpty()){
            val ticketId = ticketInfo.map { it.id }
            for (t in ticketId) {
                ticketService.cancelTicket(t!!)
            }
        }

        memberRepository.delete(memberInfo)
    }

    fun viewMyAllTicket(memberId: Long): List<TicketResponse> {
        val findTicketInfo = findTicketInfoByMemberId(memberId)
        return findTicketInfo.map { it.toResponse() }
    }


    ////////////////////////////////////[private]///////////////////////////////////////////////

    private fun saveMember(request: SignUpRequestDTO): Member {
        val (email, username, password, role) = request
        val emailRegularExpression = "^[a-zA-Z0-9]+@[a-zA-Z0-9-]+.com$"

        if (!emailRegularExpression.toRegex().matches(email)) {
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

    private fun saveKakaoMember(kakaoId: String, userNickname: String): CheckMemberResponse {
        val memberInfo = findByEmail(kakaoId)

        if (memberInfo != null) {
            return memberInfo.toResponse()
        } else {
            val userData = Member(
                email = kakaoId,
                username = userNickname,
                password = null,
                role = MemberRole.TempNameConsumer
            )
            return memberRepository.save(userData).toResponse()
        }
    }

    private fun findByEmail(email: String): Member? {
        return memberRepository.findByEmail(email)
    }

    private fun findByMemberId(memberId: Long): Member {
        val findMember = memberRepository.findByIdOrNull(memberId)
            ?: throw ModelNotFoundException("Member", memberId)

        return findMember
    }

    private fun findTicketInfoByMemberId(memberId: Long): List<Ticket> {
        return ticketRepository.findByMemberId(memberId)
            ?: throw ModelNotFoundException("Ticket", memberId)
    }


    ////////////////////////////////////[비지니스 로직 아님]///////////////////////////////////////////////
    //임시 데이터 뷰
    fun viewAllMemberData(): List<CheckMemberResponse> {
        return memberRepository.findAll().map { it.toResponse() }
    }

    fun memberRoleChange(memberId: Long): String {
        val memberInfo = findByMemberId(memberId)

        memberInfo.role = when (memberInfo.role.name) {
            "TempNameProducer" -> MemberRole.TempNameConsumer
            "TempNameConsumer" -> MemberRole.TempNameProducer
            else -> throw IllegalArgumentException("잘못된 접근입니다")
        }

        memberRepository.save(memberInfo)
        return "유저의 정보가 ${memberInfo.role.name}로 변환되었습니다"
    }
}