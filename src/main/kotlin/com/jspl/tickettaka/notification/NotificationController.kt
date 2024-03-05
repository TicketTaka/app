package com.jspl.tickettaka.notification

import com.jspl.tickettaka.exception.ResultCode
import lombok.RequiredArgsConstructor
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RequestMapping("/api/users/notification")
@RestController
@RequiredArgsConstructor
class NotificationController {
    private val notificationService: NotificationService? = null
    // 응답 시 MIME 타입을 text/event-stream으로 보냄
    //@GetMapping(value = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    @GetMapping("/subscribe")
    fun subscribe(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<SseEmitter> {
        if (ObjectUtils.isEmpty(userDetails)) {
            throw NotificationException(ResultCode.UNAUTHORIZED, "연결 시 인증이 필요합니다.")
        }
        return ResponseEntity.ok(notificationService!!.subscribe(userDetails!!.username))
    }

//    @GetMapping("/subscribe")
//    fun subscribe(authentication: Authentication): SseEmitter {
//        // Authentication을 UserDto로 업캐스팅
//        val userDto: UserDto = ClassUtils.getCastInstance(authentication.getPrincipal(), UserDto::class.java)
//            .orElseThrow {
//                ApplicationException(ResultCode.INTERNAL_SERVER_ERROR, "Casting to UserDto class failed")
//            }
//
//        // 서비스를 통해 생성된 SseEmitter를 반환
//        return notificationService!!.connectNotification(userDto.getId())
//    }
}