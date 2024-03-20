package com.jspl.tickettaka.redis

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Component

@Component
class RedisSubscriber(private val redisConnectionFactory: RedisConnectionFactory) {
    fun subscribeToChannel(channel: String) {
        val connection = redisConnectionFactory.connection
        connection.subscribe({ _: Message, message: ByteArray? ->
            val receivedMessage = message?.let { String(it) }
            println("Received message in channel '$channel': $receivedMessage")
        }, ChannelTopic(channel).topic.toByteArray())
    }
}