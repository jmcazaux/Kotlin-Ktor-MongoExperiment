
val ktor_version: String by project
val kotlin_version: String by System.getProperties()
val logback_version: String by project
val mongodb_version: String by project
val kotest_version: String by project
val testcontainers_version: String by project

plugins {
    val kotlin_version: String by System.getProperties()
    kotlin("jvm") version kotlin_version
    id("io.ktor.plugin") version "2.3.10"
    id("org.jetbrains.kotlin.plugin.serialization") version kotlin_version
}

group = "com.ironbird"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-resources")
    implementation("io.ktor:ktor-server-swagger-jvm")

    implementation("org.mongodb:bson-kotlinx:$mongodb_version")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongodb_version")

    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlin_version")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainers_version"))
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mongodb")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}
