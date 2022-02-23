# Overview

**UPDATE 2022-02-23 there are significant chunks of this that proved to be superfluous, look to these repositories for a better example: https://github.com/AnthonyHonstain/Redis-Stream-Prototype-V2 or https://bitbucket.org/honstain/redis-stream-prototype-v2** 

PLEASE don't judge me too harshly, this repo is only shared as a tool in case other people want to learn from my mistakes. Its only probably useful if it helps someone resolve similar errors, it should not be used as model to build off of. 

This Spring Boot service was created as a learning tool to help me better understand Spring Boot and the Redis streams (using the spring-data-redis package with Lettuce).

Critical components:
* Kotlin 1.4.32 with Java 11
* spring-boot-starter-data-redis 2.6.3 with spring-data-redis 2.6.1
* lettuce-core 6.1.6

### Helpful Commands

* Use redis-cli on the docker container `docker exec -it <CONTAINER ID> sh`
* Add to stream `XADD mystream * sensor-id 1234 temperature 14.0`
* Create a group for the stream `XGROUP CREATE mystream mygroup $`

# Issues

My first attempt to configure a Spring Boot service that consumed a redis stream went poorly.
I tried to set it up following https://docs.spring.io/spring-data/redis/docs/current/reference/html/#redis.streams

The guide I found more helpful for Redis Streams https://redis.io/topics/streams-intro

### Issue #1 - Failing to connect to Redis

My service would intermittently be able to process message, but would terminate with errors like the following:

```text
2022-01-31 07:33:37.073  INFO 23908 --- [           main] c.e.s.StreamConsumerDemoApplicationKt    : Starting StreamConsumerDemoApplicationKt using Java 11.0.13 on ubuntu with PID 23908 (/home/dev/Desktop/spring-basic/stream-consumer-demo/target/classes started by dev in /home/dev/Desktop/spring-basic/stream-consumer-demo)
2022-01-31 07:33:37.076  INFO 23908 --- [           main] c.e.s.StreamConsumerDemoApplicationKt    : No active profile set, falling back to default profiles: default
2022-01-31 07:33:37.493  INFO 23908 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
2022-01-31 07:33:37.494  INFO 23908 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2022-01-31 07:33:37.502  INFO 23908 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 1 ms. Found 0 Redis repository interfaces.
MessageId: 1643217642277-0
Stream: mystream
Body: {sensor-id=1234, temperature=19.6}
MessageId: 1643218128787-0
Stream: mystream
Body: {sensor-id=1234, temperature=19.3}
2022-01-31 07:33:37.913  INFO 23908 --- [           main] c.e.s.StreamConsumerDemoApplicationKt    : Started StreamConsumerDemoApplicationKt in 1.019 seconds (JVM running for 1.32)

2022-01-31 07:34:02.697 ERROR 23908 --- [cTaskExecutor-1] ageListenerContainer$LoggingErrorHandler : Unexpected error occurred in scheduled task.

org.springframework.data.redis.RedisConnectionFailureException: Unable to connect to Redis; nested exception is io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
    at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$ExceptionTranslatingConnectionProvider.translateException(LettuceConnectionFactory.java:1689) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$ExceptionTranslatingConnectionProvider.getConnection(LettuceConnectionFactory.java:1597) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.connection.lettuce.LettuceConnection.doGetAsyncDedicatedConnection(LettuceConnection.java:1007) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.connection.lettuce.LettuceConnection.getOrCreateDedicatedConnection(LettuceConnection.java:1070) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.connection.lettuce.LettuceConnection.getAsyncDedicatedConnection(LettuceConnection.java:991) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.connection.lettuce.LettuceStreamCommands.getAsyncDedicatedConnection(LettuceStreamCommands.java:397) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.connection.lettuce.LettuceStreamCommands.xReadGroup(LettuceStreamCommands.java:348) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.stream.DefaultStreamMessageListenerContainer.lambda$null$3(DefaultStreamMessageListenerContainer.java:259) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:223) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:190) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:177) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.stream.DefaultStreamMessageListenerContainer.lambda$getReadFunction$4(DefaultStreamMessageListenerContainer.java:258) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.stream.StreamPollTask.readRecords(StreamPollTask.java:166) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.stream.StreamPollTask.doLoop(StreamPollTask.java:147) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.stream.StreamPollTask.run(StreamPollTask.java:132) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at java.base/java.lang.Thread.run(Thread.java:829) ~[na:na]
Caused by: io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
    at io.lettuce.core.RedisConnectionException.create(RedisConnectionException.java:78) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at io.lettuce.core.RedisConnectionException.create(RedisConnectionException.java:56) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at io.lettuce.core.AbstractRedisClient.getConnection(AbstractRedisClient.java:330) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at io.lettuce.core.RedisClient.connect(RedisClient.java:216) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at org.springframework.data.redis.connection.lettuce.StandaloneConnectionProvider.lambda$getConnection$1(StandaloneConnectionProvider.java:115) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at java.base/java.util.Optional.orElseGet(Optional.java:369) ~[na:na]
    at org.springframework.data.redis.connection.lettuce.StandaloneConnectionProvider.getConnection(StandaloneConnectionProvider.java:115) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$ExceptionTranslatingConnectionProvider.getConnection(LettuceConnectionFactory.java:1595) ~[spring-data-redis-2.6.1.jar:2.6.1]
    ... 14 common frames omitted
Caused by: java.io.IOException: Connection reset by peer
    at java.base/sun.nio.ch.FileDispatcherImpl.read0(Native Method) ~[na:na]
    at java.base/sun.nio.ch.SocketDispatcher.read(SocketDispatcher.java:39) ~[na:na]
    at java.base/sun.nio.ch.IOUtil.readIntoNativeBuffer(IOUtil.java:276) ~[na:na]
    at java.base/sun.nio.ch.IOUtil.read(IOUtil.java:233) ~[na:na]
    at java.base/sun.nio.ch.IOUtil.read(IOUtil.java:223) ~[na:na]
    at java.base/sun.nio.ch.SocketChannelImpl.read(SocketChannelImpl.java:356) ~[na:na]
    at io.netty.buffer.PooledByteBuf.setBytes(PooledByteBuf.java:258) ~[netty-buffer-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.buffer.AbstractByteBuf.writeBytes(AbstractByteBuf.java:1132) ~[netty-buffer-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.channel.socket.nio.NioSocketChannel.doReadBytes(NioSocketChannel.java:350) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:151) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:722) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:658) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:584) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:496) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.util.concurrent.SingleThreadEventExecutor$4.run(SingleThreadEventExecutor.java:986) ~[netty-common-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.util.internal.ThreadExecutorMap$2.run(ThreadExecutorMap.java:74) ~[netty-common-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30) ~[netty-common-4.1.73.Final.jar:4.1.73.Final]
    ... 1 common frames omitted
Process finished with exit code 0
```

I also saw this error

```text
Caused by: io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
    at io.lettuce.core.RedisConnectionException.create(RedisConnectionException.java:78) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at io.lettuce.core.RedisConnectionException.create(RedisConnectionException.java:56) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at io.lettuce.core.AbstractRedisClient.getConnection(AbstractRedisClient.java:330) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at io.lettuce.core.RedisClient.connect(RedisClient.java:216) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at org.springframework.data.redis.connection.lettuce.StandaloneConnectionProvider.lambda$getConnection$1(StandaloneConnectionProvider.java:115) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at java.base/java.util.Optional.orElseGet(Optional.java:369) ~[na:na]
    at org.springframework.data.redis.connection.lettuce.StandaloneConnectionProvider.getConnection(StandaloneConnectionProvider.java:115) ~[spring-data-redis-2.6.1.jar:2.6.1]
    at org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory$ExceptionTranslatingConnectionProvider.getConnection(LettuceConnectionFactory.java:1595) ~[spring-data-redis-2.6.1.jar:2.6.1]
    ... 15 common frames omitted
Caused by: io.lettuce.core.RedisConnectionException: Connection closed prematurely
    at io.lettuce.core.protocol.RedisHandshakeHandler.channelInactive(RedisHandshakeHandler.java:86) ~[lettuce-core-6.1.6.RELEASE.jar:6.1.6.RELEASE]
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:262) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.channel.AbstractChannelHandlerContext.invokeChannelInactive(AbstractChannelHandlerContext.java:248) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
    at io.netty.channel.AbstractChannelHandlerContext.fireChannelInactive(AbstractChannelHandlerContext.java:241) ~[netty-transport-4.1.73.Final.jar:4.1.73.Final]
```

OLD VERSION I had this for a docker-compose config
```yaml
  redis:
    hostname: redis
    image: "redis:alpine"
    ports:
      - "6379:6379"
```

**FIXED VERSION** - I add a config and forced it to listen to all interfaces
```yaml
  redis:
    hostname: redis
    image: "redis:alpine"
    ports:
      - "6379:6379"
    volumes:
      - ./redis.conf:/redis.conf
    command: [ "redis-server", "/redis.conf" ]
```

`redis.conf`

```
################################## NETWORK #####################################

# By default, if no "bind" configuration directive is specified, Redis listens
# for connections from all the network interfaces available on the server.
# It is possible to listen to just one or multiple selected interfaces using
# the "bind" configuration directive, followed by one or more IP addresses.
#
# Examples:
#
# bind 192.168.1.100 10.0.0.1
# bind 127.0.0.1 ::1
#
# ~~~ WARNING ~~~ If the computer running Redis is directly exposed to the
# internet, binding to all the interfaces is dangerous and will expose the
# instance to everybody on the internet. So by default we uncomment the
# following bind directive, that will force Redis to listen only into
# the IPv4 lookback interface address (this means Redis will be able to
# accept connections only from clients running into the same computer it
# is running).
#
# IF YOU ARE SURE YOU WANT YOUR INSTANCE TO LISTEN TO ALL THE INTERFACES
# JUST COMMENT THE FOLLOWING LINE.
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# bind 127.0.0.1
# bind 0.0.0.0
```

I also had to adjust how I setup my subscription,
* https://groups.google.com/g/lettuce-redis-client-users/c/4o3kKHdh6Yc
* https://docs.spring.io/spring-data/redis/docs/current/reference/html/#redis:template

### Issue #2 - Shutdown

I noticed that my service would throw errors during shutdown 

```text
2022-02-02 18:14:43.529  WARN 43190 --- [ionShutdownHook] io.lettuce.core.RedisChannelHandler      : Connection is already closed
2022-02-02 18:14:43.536 ERROR 43190 --- [cTaskExecutor-1] ageListenerContainer$LoggingErrorHandler : Unexpected error occurred in scheduled task.
				
org.springframework.data.redis.RedisConnectionFailureException: Unable to connect to Redis; nested exception is io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
Caused by: io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
Caused by: java.lang.IllegalStateException: executor not accepting a task
```

I experimented with adding `server.shutdown=graceful` to the `application.properties`, which gave me:
```text
2022-02-02 18:13:14.256  WARN 43007 --- [cTaskExecutor-1] io.lettuce.core.RedisChannelHandler      : Connection is already closed
2022-02-02 18:13:14.262  WARN 43007 --- [cTaskExecutor-1] io.netty.channel.AbstractChannel         : Force-closing a channel whose registration task was not accepted by an event loop: [id: 0x8e75e09f]

java.util.concurrent.RejectedExecutionException: event executor terminated
2022-02-02 18:13:14.262 ERROR 43007 --- [cTaskExecutor-1] i.n.u.c.D.rejectedExecution              : Failed to submit a listener notification task. Event loop shut down?
java.util.concurrent.RejectedExecutionException: event executor terminated
```

I found this reference which seemed applicable https://github.com/spring-projects/spring-data-redis/issues/2246
BUT there was not configuration of bean shutdown that I found which got me past the issue.

I traced it to the fact that the subscription can take a significant amount of time to shutdown, which depends on how long you configured the pollTimeout on the `StreamMessageListenerContainerOptions`

```kotlin
        val containerOptions = StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofMillis(1000))
                .build()
```

The code I added to my bean that implements the `DisposableBean` interface.
 
```kotlin
    override fun destroy() {
        println("Destroy ContainerSubscription - sub: ${subscription?.isActive}")

        // Timing how long it takes https://stackoverflow.com/questions/1770010/how-do-i-measure-time-elapsed-in-java
        val startTime = System.nanoTime()
        subscription?.cancel()

        while (subscription?.isActive == true) {
            //println("wait... 10ms")
            Thread.sleep(1)
        }
        println("Time required to isActive==false : ${TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)} ms")
        println("Destroy ContainerSubscription - sub: ${subscription?.isActive}")

    }
```


# Docker Compose

I used the following docker compose script to setup a local environment that had Redis with streams capability. 
It also had redisinsight to service as a visual tool for interacting with the stream.

`docker-compose.yml`
```yaml
version: "3.3"
# https://docs.docker.com/compose/compose-file/compose-versioning/

services:

  redis:
    # Reference:
    #   https://hub.docker.com/_/redis
    hostname: redis
    image: "redis:alpine"
    ports:
      - "6379:6379"
    volumes:
      - ./redis.conf:/redis.conf
    command: [ "redis-server", "/redis.conf" ]

  redisinsight:
    # Reference:
    #   https://docs.redis.com/latest/ri/installing/install-docker/
    #
    # REMEMBER - to connect to the redis database, use the host: "redis"
    image: "redislabs/redisinsight:latest"
    ports:
      - "8001:8001"
```

`redis.conf`
```text
# Redis configuration file example.
#
# Note that in order to read the configuration file, Redis must be
# started with the file path as first argument:
#
# ./redis-server /path/to/redis.conf

################################## NETWORK #####################################

# By default, if no "bind" configuration directive is specified, Redis listens
# for connections from all the network interfaces available on the server.
# It is possible to listen to just one or multiple selected interfaces using
# the "bind" configuration directive, followed by one or more IP addresses.
#
# Examples:
#
# bind 192.168.1.100 10.0.0.1
# bind 127.0.0.1 ::1
#
# ~~~ WARNING ~~~ If the computer running Redis is directly exposed to the
# internet, binding to all the interfaces is dangerous and will expose the
# instance to everybody on the internet. So by default we uncomment the
# following bind directive, that will force Redis to listen only into
# the IPv4 lookback interface address (this means Redis will be able to
# accept connections only from clients running into the same computer it
# is running).
#
# IF YOU ARE SURE YOU WANT YOUR INSTANCE TO LISTEN TO ALL THE INTERFACES
# JUST COMMENT THE FOLLOWING LINE.
# ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# bind 127.0.0.1
# bind 0.0.0.0

# TODO - I just copied the relevant bit from https://github.com/ashutoshkarna03/redis-docker-python/blob/master/config/redis.conf

```