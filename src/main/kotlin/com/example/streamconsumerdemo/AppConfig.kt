package com.example.streamconsumerdemo

import io.lettuce.core.api.StatefulRedisConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.stream.*
import org.springframework.data.redis.connection.stream.StreamOffset.fromStart
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.Subscription
import java.time.Duration
import javax.annotation.PreDestroy

@Configuration
class AppConfig {

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(
                RedisStandaloneConfiguration("localhost", 6379)
        )
    }

    @PreDestroy
    fun foo() {
        println("Shutdown AppConfig")
    }


}