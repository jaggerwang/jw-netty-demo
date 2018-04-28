# JW Netty Demo

This demo implemented a tcp protocol server, which receiving and storing monitor data gathered by sensor on hardware device. Multiple sensors can be connected to one device, and device will report data items of all sensors at one time. The server need to compute average value of all data items reported at one time. 

The server is developed using the [Netty](https://netty.io/) famework, so the performance is good. There also have docker files for deploying.

## Protocol

The binary protocol used to transfer one message is as follows.

| start(2 bytes) | total length(2 bytes) | version(1 byte) | device no(4 bytes) | time(4 bytes) | data item length(1 byte) | data item value | more data ... | checksum(2 bytes) |

* all segments are unsigned numbers and in network byte order
* start bytes fixed to `0x55 0xaa`
* total length is the number of bytes included in message
* time segment are unix timestamp
* byte length of date item value is controlled by it's previous segment's value
* date item and valus pair can repeat many times, as if the total length not exceed 65535

## Develop

### Building tool

This project use [Gradle](https://gradle.org/) as the building tool. There is no need to install Gradle at first. When first execute `./gradlew <task>` command in the root directory of project, it will auto download and install gradle for this project.

The `build.gradle` file for this project is as follows. It uses `application` gradle plugin to build the project.

```groovy
apply plugin : 'application'

mainClassName = 'net.jaggerwang.jwnettydemo.Main'

dependencies {
    compile 'io.netty:netty-all:4.1.21.Final'
    compile 'org.apache.logging.log4j:log4j-api:2.10.0'
    compile 'org.apache.logging.log4j:log4j-core:2.10.0'
    compile 'org.mongodb:mongodb-driver:3.6.3'
    compile 'com.aliyun:hitsdb-client:0.0.5'
    compile 'org.apache.kafka:kafka-clients:0.10.0.0'
    compile 'org.apache.kafka:connect-json:0.10.0.0'
    compile 'com.aliyun.openservices:ons-sasl-client:0.1'
    compile 'com.fasterxml.jackson.core:jackson-core:2.9.4'
    compile 'com.fasterxml.jackson.core:jackson-databind:2.9.4'
    compile 'com.fasterxml.jackson.module:jackson-module-parameter-names:2.9.4'
    compile 'com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.9.4'
    compile 'com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.4'
    compile 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'
    compile 'commons-codec:commons-codec:1.10'

    compileOnly 'org.projectlombok:lombok:1.16.18'

    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.1.0'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.1.0'
}

repositories {
    mavenCentral()
}

test {
    useJUnitPlatform()
}

```

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

After run, you can post binary message to address `localhost:8080` now.

### Test

```bash
$ ./gradlew test
  Starting a Gradle Daemon (subsequent builds will be faster)
  
  Deprecated Gradle features were used in this build, making it incompatible with Gradle 5.0.
  See https://docs.gradle.org/4.6/userguide/command_line_interface.html#sec:command_line_warnings
  
  BUILD SUCCESSFUL in 7s
  4 actionable tasks: 4 up-to-date
```

This project use JUnit 5 for unit test. There have two unit tests, one is for message decoder, and the other is for message saver.

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

The app build is installed in path `./build/install/jw-netty-demo`, and there is a script `bin/jw-netty-demo` to run it.

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
