FROM azul/zulu-openjdk-alpine:11
ARG JAR_FILE=./target/*.jar
WORKDIR /app
COPY ${JAR_FILE} ./app.jar
ENTRYPOINT java -jar app.jar