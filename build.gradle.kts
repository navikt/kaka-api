import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val logstashVersion = "7.2"
val problemSpringWebStartVersion = "0.27.0"
val springSleuthVersion = "3.1.5"
val tokenValidationVersion = "2.1.8"
val archunitVersion = "1.0.1"
val testContainersVersion = "1.17.6"
val mockkVersion = "1.13.2"
val apachePoiVersion = "5.2.3"
val springDocVersion = "1.6.13"

plugins {
    val kotlinVersion = "1.7.21"
    id("org.springframework.boot") version "2.7.6"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
}

apply(plugin = "io.spring.dependency-management")

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("no.nav.security:token-validation-spring:$tokenValidationVersion")
    implementation("no.nav.security:token-client-spring:$tokenValidationVersion")
    implementation("org.zalando:problem-spring-web-starter:$problemSpringWebStartVersion")
    implementation("org.springdoc:springdoc-openapi-ui:$springDocVersion")
    implementation("org.flywaydb:flyway-core")
    implementation("org.postgresql:postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("ch.qos.logback:logback-classic")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("org.projectreactor:reactor-spring:1.0.1.RELEASE")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth:$springSleuthVersion")

    implementation("org.apache.poi:poi:$apachePoiVersion")
    implementation("org.apache.poi:poi-ooxml:$apachePoiVersion")

    implementation("com.github.navikt:klage-kodeverk:1.1.8")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation("com.tngtech.archunit:archunit-junit5:$archunitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    this.archiveFileName.set("app.jar")
}
