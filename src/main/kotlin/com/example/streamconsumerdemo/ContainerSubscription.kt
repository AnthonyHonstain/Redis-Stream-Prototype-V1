package com.example.streamconsumerdemo

import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.DependsOn
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.Subscription
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy


@Component
class ContainerSubscription(
        val container: StreamMessageListenerContainer<String, MapRecord<String, String, String>>,
        val streamListener: ExampleStreamListener,
): ApplicationRunner, DisposableBean {

    var subscription: Subscription? = null

    @PreDestroy
    fun foo() {
        println("PreDestroy ContainerSubscription - sub: ${subscription?.isActive}")
        subscription?.cancel()
        //container.stop()
    }

    //@Bean
    //fun containerSubscription1(
    //        streamListener: ExampleStreamListener,
    //        //container: StreamMessageListenerContainer<String, MapRecord<String, String, String>>,
    //        //redisConnectionFactory: RedisConnectionFactory,
    //): Subscription {
    //
    //    //val containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder().pollTimeout(Duration.ofMillis(100)).build()
    //    //val container = StreamMessageListenerContainer.create(redisConnectionFactory, containerOptions)
    //
    //    val consumer = Consumer.from("mygroup", "Alice")
    //    val subscription: Subscription? = container.receive(
    //            consumer,
    //            StreamOffset.create("mystream", ReadOffset.lastConsumed()),
    //            streamListener
    //    )
    //
    //    // This one will read from the very beginning of the stream.
    //    //val subscription: Subscription? = container.receive(StreamOffset.fromStart("mystream"), streamListener)
    //
    //    container.start()
    //
    //    // todo - I haven't found a Kotlin null handling that I like yet.
    //    if (subscription != null) {
    //        return subscription
    //    }
    //    throw Exception("Failed to create subscription")
    //}

    override fun run(args: ApplicationArguments?) {
        val consumer = Consumer.from("mygroup", "Alice")
        subscription = container.receive(
                consumer,
                StreamOffset.create("mystream", ReadOffset.lastConsumed()),
                streamListener
        )

        // This one will read from the very beginning of the stream.
        //val subscription: Subscription? = container.receive(StreamOffset.fromStart("mystream"), streamListener)

        container.start()

        // todo - I haven't found a Kotlin null handling that I like yet.
        //if (subscription != null) {
        //    return subscription
        //}
        //throw Exception("Failed to create subscription")
    }

    override fun destroy() {
        println("Destroy ContainerSubscription - sub: ${subscription?.isActive}")
        val startTime = System.nanoTime()
        subscription?.cancel()

        while (subscription?.isActive == true) {
            //println("wait... 10ms")
            Thread.sleep(1)
        }
        println("Time required to isActive==false : ${TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)} ms")
        println("Destroy ContainerSubscription - sub: ${subscription?.isActive}")

    }
}