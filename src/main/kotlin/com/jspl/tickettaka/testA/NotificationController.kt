package com.jspl.tickettaka.testA

import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDateTime

@RestController
class NotificationController() {
    @GetMapping("/notifications")
    fun getNotifications(): Flux<ServerSentEvent<String>> {
        return Flux.interval(Duration.ofSeconds(1))
            .map {
                ServerSentEvent.builder<String>()
                    .id(it.toString())
                    .event("notification")
                    .data("New notification at ${LocalDateTime.now()}")
                    .build()
            }
    }
}