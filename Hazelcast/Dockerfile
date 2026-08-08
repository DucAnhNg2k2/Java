# ---------- Build ----------
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY gradle gradle
COPY gradlew settings.gradle.kts build.gradle.kts ./
# gradlew có thể bị lưu dạng CRLF trên Windows, phải chuẩn hoá về LF mới chạy được trên Linux
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Tách bước tải dependency ra layer riêng để lần build sau được cache lại
RUN ./gradlew --no-daemon dependencies --configuration runtimeClasspath > /dev/null

COPY src src
RUN ./gradlew --no-daemon bootJar -x test

# ---------- Runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --create-home --shell /bin/bash app
COPY --from=build /workspace/build/libs/*.jar app.jar
USER app

# 8080: HTTP  |  5701: kênh giao tiếp giữa các Hazelcast member
EXPOSE 8080 5701

# Các --add-opens/--add-exports là bắt buộc cho Hazelcast member trên Java 17+
ENTRYPOINT ["java", \
    "--add-modules", "java.se", \
    "--add-exports", "java.base/jdk.internal.ref=ALL-UNNAMED", \
    "--add-opens", "java.base/java.lang=ALL-UNNAMED", \
    "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", \
    "--add-opens", "java.management/sun.management=ALL-UNNAMED", \
    "--add-opens", "jdk.management/com.sun.management.internal=ALL-UNNAMED", \
    "-jar", "app.jar"]
