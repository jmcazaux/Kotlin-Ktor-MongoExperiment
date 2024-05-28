val ktorVersion: String by System.getProperties()
val kotlinVersion: String by System.getProperties()
val logbackVersion: String by project
val mongodbVersion: String by project
val kotestVersion: String by project
val testcontainersVersion: String by project
val mockkVersion: String by project

plugins {
    val kotlinVersion: String by System.getProperties()
    val ktorVersion: String by System.getProperties()
    kotlin("jvm") version kotlinVersion
    id("io.ktor.plugin") version ktorVersion
    id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
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
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")

    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-resources")
    implementation("io.ktor:ktor-server-swagger-jvm")

    implementation("org.mongodb:bson-kotlinx:$mongodbVersion")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongodbVersion")


    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")

    testImplementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests-jvm")

    testImplementation("io.mockk:mockk:${mockkVersion}")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$kotlinVersion")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))
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
