plugins {
    id("java")
    id("application")
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

dependencies {
    implementation("com.hazelcast:hazelcast:5.7.0")

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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

application {
    mainClass = "org.example.Main"
    applicationDefaultJvmArgs = hazelcastJvmArgs
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(hazelcastJvmArgs)
}
