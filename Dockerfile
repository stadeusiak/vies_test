FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY build/libs/app.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=3s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8080/swagger-ui/index.html || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]