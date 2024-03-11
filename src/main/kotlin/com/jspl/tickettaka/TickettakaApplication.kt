package com.jspl.tickettaka

import com.jspl.tickettaka.redis.RedisPublisher
import com.jspl.tickettaka.redis.RedisSubscriber
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class TickettakaApplication {
    @Bean
    fun redisPublisher(redisTemplate: RedisTemplate<String, String>) = RedisPublisher(redisTemplate)
    @Bean
    fun redisSubscriber(redisConnectionFactory: RedisConnectionFactory): RedisSubscriber {
        return RedisSubscriber(redisConnectionFactory)
    }
}

fun main(args: Array<String>) {
    runApplication<TickettakaApplication>(*args)

    val restTemplate = RestTemplate()
    // Publish notification message to "notifications" channel
    val message = "New notification message"
    val publishResponse = restTemplate.postForObject(
        "http://localhost:8080/users/redis/publish/notifications",
        message,
        String::class.java
    )
    println("Published notification message: $message")

    // Subscribe to "notifications" channel
    val subscribeResponse = restTemplate.getForObject(
        "http://localhost:8080/users/redis/subscribe/notifications",
        String::class.java
    )
    println("Subscribed to notifications channel: $subscribeResponse")
}
