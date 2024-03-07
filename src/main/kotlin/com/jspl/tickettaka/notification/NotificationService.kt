package com.jspl.tickettaka.notification

import com.jspl.tickettaka.exception.MsgFormat
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.io.IOException
import javax.management.Notification


@Service
class NotificationService(
    private val sseRepository: EmitterRepository,
    private val notificationRepository: NotificationRepository,
    private val authenticationService: AuthenticationService
) {

    private val log = LoggerFactory.getLogger(NotificationService::class.java)

    @Value("\${sse.timeout}")
    private val timeout: Long = 60000 // emitter 만료 시간 -> 1분으로 설정

    fun subscribe(memberKey: String): SseEmitter {
        val sseEmitter = SseEmitter(timeout) // emitter 생성
        sseRepository.save(memberKey, sseEmitter)

        // emitter handling
        sseEmitter.onTimeout { sseEmitter.complete() }
        sseEmitter.onError { e -> sseEmitter.complete() }
        sseEmitter.onCompletion { sseRepository.deleteById(memberKey) }

        // dummy data 전송
        send(MsgFormat.SUBSCRIBE, memberKey, sseEmitter)
        return sseEmitter
    }

    private fun send(data: Any, emitterKey: String, sseEmitter: SseEmitter) {
        try {
            log.info("send to client {}:[{}]", emitterKey, data)
            // 이벤트 데이터 전송
            sseEmitter.send(SseEmitter.event()
                .id(emitterKey)
                .data(data, MediaType.APPLICATION_JSON)) // data가 메시지만 포함된다면 타입을 지정해줄 필요없음
        } catch (e: IOException) {
            log.error("IOException is occurred. ", e)
            sseRepository.deleteById(emitterKey)
        } catch (e: IllegalStateException) {
            log.error("IllegalStateException is occurred. ", e)
            sseRepository.deleteById(emitterKey)
        }
    }

    @Transactional
    fun sendNotification(event: NotificationEvent) {
        val key = event.memberKey
        val member = authenticationService.getMemberOrThrow(key)
        val notification = Notification.of(event.message, event.notificationType, event.relatedUri)
        notification.addMember(member)
        notificationRepository.save(notification)

        // 유저와 연결된 emitter를 찾는다.
        sseRepository.findById(key)
            .ifPresent { emitter -> send(NotificationDto.fromEntity(notification), key, emitter) } // 데이터 전송
    }
}