FROM azul/zulu-openjdk-alpine:11 AS builder
WORKDIR /app
COPY . .
RUN apk --no-cache add maven && \
    mvn clean install

FROM azul/zulu-openjdk-alpine:11
WORKDIR /app
COPY --from=builder /app/target/gcp-demo-0.0.1-SNAPSHOT.jar ./app.jar
CMD java -jar app.jar