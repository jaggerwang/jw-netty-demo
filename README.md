# JW Netty Demo

This demo implemented a tcp protocol server, which receiving and storing monitor data gathered by sensor on hardware device. Multiple sensors can be connected to one device, and device will report data items of all sensors at one time. The server need to compute average value of all data items reported at one time. 

The server is developed using the [Netty](https://netty.io/) famework, so the performance is good. There also have docker files for deploying.

## Protocol

The binary protocol used to transfer one message is as follows.

| start(2 bytes) | total length(2 bytes) | version(1 byte) | device no(4 bytes) | time(4 bytes) | data item length(1 byte) | data item value(data item length byte or bytes) | more data ... | checksum(2 bytes) |

* all segments are unsigned numbers and in network byte order
* start bytes fixed to `0x55 0xaa`
* total length is the number of bytes included in message
* time segment are unix timestamp
* date item value's length is determined by it's previous segment's value
* date item and valus pair can repeat many times, as if the total length not exceed 65535

## Develop

### Run

```bash
$ ./gradlew run
  Starting a Gradle Daemon (subsequent builds will be faster)
  
  > Task :run
  2018-四月-26 21:21:49 INFO  net.jaggerwang.jwnettydemo.config.ApplicationConfig - load properties ok
  SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
  SLF4J: Defaulting to no-operation (NOP) logger implementation
  SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
  2018-四月-26 21:21:50 INFO  net.jaggerwang.jwnettydemo.Main - server started on port 8080
  <=========----> 75% EXECUTING [17s]
  > :run
```

### Test

```bash
$ ./gradlew test
  Starting a Gradle Daemon (subsequent builds will be faster)
  
  Deprecated Gradle features were used in this build, making it incompatible with Gradle 5.0.
  See https://docs.gradle.org/4.6/userguide/command_line_interface.html#sec:command_line_warnings
  
  BUILD SUCCESSFUL in 7s
  4 actionable tasks: 4 up-to-date
```

### Package

```bash
$ ./gradlew installDist
  
  Deprecated Gradle features were used in this build, making it incompatible with Gradle 5.0.
  See https://docs.gradle.org/4.6/userguide/command_line_interface.html#sec:command_line_warnings
  
  BUILD SUCCESSFUL in 2s
  5 actionable tasks: 3 executed, 2 up-to-date

$ ./build/install/jw-netty-demo/bin/jw-netty-demo
  2018-四月-26 22:23:22 INFO  net.jaggerwang.jwnettydemo.config.ApplicationConfig - load properties ok
  SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
  SLF4J: Defaulting to no-operation (NOP) logger implementation
  SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
  2018-四月-26 22:23:22 INFO  net.jaggerwang.jwnettydemo.Main - server started on port 8080
```

## Deploy

### Build image

```bash
$ docker build -t jw-netty-demo .
```

Docker build use the following Dcokerfile to build image.

```dockerfile
FROM java:8

ENV APP_PATH=/app
ENV DATA_PATH=/data

WORKDIR $APP_PATH

COPY . .
RUN ./gradlew installDist

VOLUME $DATA_PATH

EXPOSE 8080

CMD ./build/install/jw-netty-demo/bin/jw-netty-demo

```

### Run containers

```bash
$ docker-compose up
```

Docker compose use the following config file to create and start app and mongodb containers. The app container depended on the mongodb container.

```yaml
version: "2"
services:
  app:
    image: jw-netty-demo:latest
    environment:
      PATH_APP: /app
      PATH_DATA: /data
      MONGODB_URI: mongodb:27017
    ports:
    - 19900:8080
    volumes:
    - ~/data/jw-netty-demo/app:/data
  mongodb:
    image: mongo:3
    volumes:
    - ~/data/jw-netty-demo/mongodb:/data/db

```
