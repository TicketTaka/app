package com.jspl.tickettaka.controller

//@RequestMapping("/api/users/notification")
//@RestController
//@RequiredArgsConstructor
//class NotificationApi {
//    private val notificationService: NotificationService? = null
//    // 응답 시 MIME 타입을 text/event-stream으로 보냄
//    @GetMapping(value = ["/subscribe"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
//    fun subscribe(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<SseEmitter> {
//        if (ObjectUtils.isEmpty(userDetails)) {
//            throw NotificationException(ResultCode.UNAUTHORIZED, "연결 시 인증이 필요합니다.")
//        }
//        return ResponseEntity.ok(notificationService!!.subscribe(userDetails!!.username))
//    }
//
////    @GetMapping("/subscribe")
////    fun subscribe(authentication: Authentication): SseEmitter {
////        // Authentication을 UserDto로 업캐스팅
////        val userDto: UserDto = ClassUtils.getCastInstance(authentication.getPrincipal(), UserDto::class.java)
////            .orElseThrow {
////                ApplicationException(ResultCode.INTERNAL_SERVER_ERROR, "Casting to UserDto class failed")
////            }
////
////        // 서비스를 통해 생성된 SseEmitter를 반환
////        return notificationService!!.connectNotification(userDto.getId())
////    }
//
//    @GetMapping("/events")
//    fun getEvents(): Flux<ServerSentEvent<String>> {
//        return Flux.interval(Duration.ofSeconds(1))
//            .map { id -> ServerSentEvent.builder("Event message $id").build() }
//    }
//}