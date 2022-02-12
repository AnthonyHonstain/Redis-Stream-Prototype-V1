package com.example.streamconsumerdemo

import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.Subscription
import org.springframework.stereotype.Component
import java.time.Duration
import javax.annotation.PreDestroy
import kotlin.concurrent.thread


@Component
class RedisContainer(
        val redisConnectionFactory: RedisConnectionFactory,
        //val streamListener: ExampleStreamListener,
): DisposableBean {

    var container: StreamMessageListenerContainer<String, MapRecord<String, String, String>>? = null
    //var subscription: Subscription? = null

    @PreDestroy
    fun foo() {
        println("PreDestroy RedisContainer - container: ${container?.isRunning}")// sub: ${subscription?.isActive}")
        //container?.stop()
    }

    @Bean
    fun container(
            //redisConnectionFactory: RedisConnectionFactory,
    ): StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        val containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofMillis(1000))
                .build()
        container = StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions)
        return container!!
    }

    //override fun run(args: ApplicationArguments?) {
    //    val containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder().pollTimeout(Duration.ofMillis(10)).build()
    //    container = StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions)
    //
    //    val consumer = Consumer.from("mygroup", "Alice")
    //    subscription = container?.receive(
    //            consumer,
    //            StreamOffset.create("mystream", ReadOffset.lastConsumed()),
    //            streamListener
    //    )
    //
    //    // This one will read from the very beginning of the stream.
    //    //val subscription: Subscription? = container.receive(StreamOffset.fromStart("mystream"), streamListener)
    //
    //    container?.start()
    //
    //    //// todo - I haven't found a Kotlin null handling that I like yet.
    //    //if (subscription != null) {
    //    //    return subscription
    //    //}
    //    //throw Exception("Failed to create subscription")
    //}

    override fun destroy() {
        println("Destroy RedisContainer - container: ${container?.isRunning}")// sub: ${subscription?.isActive}")
        //subscription?.cancel()
        //Thread.sleep(1000)

        container?.stop()
        println("Destroy RedisContainer - container: ${container?.isRunning}")// sub: ${subscription?.isActive}")
        //Thread.sleep(1000)
    }

}