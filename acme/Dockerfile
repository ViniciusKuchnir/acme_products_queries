FROM openjdk:17-alpine

RUN apk add --no-cache netcat-openbsd

WORKDIR /app

COPY target/ACME-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8090 

ENTRYPOINT ["java", "-jar", "app.jar"]
