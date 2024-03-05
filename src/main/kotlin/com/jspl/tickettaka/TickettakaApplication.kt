package com.jspl.tickettaka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub

@SpringBootApplication
class TickettakaApplication

class MessageSubscriber : JedisPubSub() {
    override fun onMessage(channel: String?, message: String?) {
        println("Received message: $message from channel: $channel")
    }
}

fun main(args: Array<String>) {
    val jedis = Jedis("localhost") // Redis 서버에 연결합니다.
    jedis.publish("channel", "메시지 본문") // "channel" 채널로 메시지를 발행합니다.
    jedis.close() // 연결을 닫습니다.

    val subscriber = MessageSubscriber()
    jedis.subscribe(subscriber, "channel") // "channel" 채널을 구독합니다.
    // 구독을 유지하기 위해 이 코드는 계속 실행됩니다.

    runApplication<TickettakaApplication>(*args)
}
