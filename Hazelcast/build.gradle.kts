plugins {
    id("java")
    id("org.springframework.boot") version "4.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

val springBootBom = "org.springframework.boot:spring-boot-dependencies:4.1.0"

dependencies {
    implementation(platform(springBootBom))
    // annotationProcessor không kế thừa implementation nên phải khai BOM riêng,
    // nếu không spring-boot-configuration-processor sẽ thiếu version
    annotationProcessor(platform(springBootBom))

    implementation("org.springframework.boot:spring-boot-starter-web")
    // Spring Boot BOM quản Hazelcast 5.5.0, pin đè lên bản mới nhất
    implementation("com.hazelcast:hazelcast:5.7.0")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Hazelcast member cần mở một số package nội bộ của JDK khi chạy trên Java 17+
val hazelcastJvmArgs = listOf(
    "--add-modules", "java.se",
    "--add-exports", "java.base/jdk.internal.ref=ALL-UNNAMED",
    "--add-opens", "java.base/java.lang=ALL-UNNAMED",
    "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens", "java.management/sun.management=ALL-UNNAMED",
    "--add-opens", "jdk.management/com.sun.management.internal=ALL-UNNAMED"
)

tasks.bootRun {
    jvmArgs(hazelcastJvmArgs)
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(hazelcastJvmArgs)
}
