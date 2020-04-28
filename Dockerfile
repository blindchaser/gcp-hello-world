FROM azul/zulu-openjdk-alpine:11 AS builder
ARG PROJECT_ID
ARG SPANNER_DATABASE
ARG SPANNER_INSTANCE_ID
WORKDIR /app
COPY . .
RUN apk --no-cache add maven && \
    mvn clean install

FROM azul/zulu-openjdk-alpine:11
ARG PROJECT_ID
ARG SPANNER_DATABASE
ARG SPANNER_INSTANCE_ID
ARG JAR_FILE=/app/target/*.jar
WORKDIR /app
COPY --from=builder ${JAR_FILE} ./app.jar
CMD java -jar app.jar