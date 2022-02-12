package com.example.streamconsumerdemo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamListener
import org.springframework.stereotype.Component
import javax.annotation.PreDestroy

@Component
class ExampleStreamListener(
        var redisTemplate: StringRedisTemplate,
): StreamListener<String, MapRecord<String, String, String>> {

    @PreDestroy
    fun foo() {
        println("Shutdown ExampleStreamListener")
    }


    override fun onMessage(message: MapRecord<String, String, String>) {
        System.out.println("MessageId: " + message.id);
        System.out.println("Stream: " + message.stream);
        System.out.println("Body: " + message.value);

        redisTemplate.opsForStream<String, String>().acknowledge("mygroup", message)
    }
}