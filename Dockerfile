FROM azul/zulu-openjdk-alpine:11 AS builder
COPY . .
RUN apk --no-cache add maven && \
    mvn clean install

FROM azul/zulu-openjdk-alpine:11
COPY --from=builder /target/gcp-demo-0.0.1-SNAPSHOT.jar app.jar
CMD java -jar app.jar