FROM java:8

ENV APP_PATH=/app
ENV DATA_PATH=/data

WORKDIR $APP_PATH

COPY . .
RUN ./gradlew installDist

VOLUME $DATA_PATH

EXPOSE 8080

CMD ./build/install/jw-netty-demo/bin/jw-netty-demo
