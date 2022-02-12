# Overview

This Spring Boot service was created as a learning tool to help me better understand Spring Boot and the Redis streams (using the spring-data-redis package with Lettuce).

Critical components:
* Kotlin 1.4.32 with Java 11
* spring-boot-starter-data-redis 2.6.3 with spring-data-redis 2.6.1
* lettuce-core 6.1.6

# Issues

My first attempt to configure a Spring Boot service that consumed a redis stream went poorly.
I tried to set it up following https://docs.spring.io/spring-data/redis/docs/current/reference/html/#redis.streams

The guide I found more helpful for Redis Streams https://redis.io/topics/streams-intro

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
    #expose:
    #  - 6379
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