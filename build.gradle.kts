import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

val ktlintVersion = "1.8.0"
val logstashVersion = "9.0"
val tokenValidationVersion = "6.0.12"
val archunitVersion = "1.5.0"
val testContainersVersion = "2.0.5"
val mockkVersion = "1.14.11"
val apachePoiVersion = "5.5.1"
val springDocVersion = "3.1.0"
val shedlockVersion = "7.9.0"
val klageKodeverkVersion = "3.3.20"
val ehcacheVersion = "3.12.0"
val otelVersion = "1.65.0"

plugins {
    val kotlinVersion = "2.4.10"
    id("org.springframework.boot") version "4.1.1"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
    id("dev.detekt") version "2.0.0-alpha.6"
}

apply(plugin = "io.spring.dependency-management")

java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
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
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("no.nav.security:token-validation-spring:$tokenValidationVersion")
    implementation("no.nav.security:token-client-spring:$tokenValidationVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocVersion")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.postgresql:postgresql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("ch.qos.logback:logback-classic")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("org.projectreactor:reactor-spring:1.0.1.RELEASE")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")
    implementation("net.javacrumbs.shedlock:shedlock-spring:$shedlockVersion")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:$shedlockVersion")

    implementation("io.opentelemetry:opentelemetry-api:$otelVersion")

    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("javax.cache:cache-api")
    implementation("org.ehcache:ehcache:$ehcacheVersion")

    implementation("io.micrometer:micrometer-registry-prometheus")

    implementation("org.apache.poi:poi:$apachePoiVersion")
    implementation("org.apache.poi:poi-ooxml:$apachePoiVersion")

    implementation("no.nav.klage:klage-kodeverk:$klageKodeverkVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test") {
        exclude(group = "org.junit.vintage")
        exclude(group = "org.mockito")
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:testcontainers-postgresql:$testContainersVersion")
    testImplementation("com.tngtech.archunit:archunit-junit5:$archunitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

ktlint {
    version.set(ktlintVersion)
    ignoreFailures.set(false)
    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
    }
    filter {
        exclude { it.file.path.contains("${File.separator}build${File.separator}") }
    }
}

detekt {
    config.setFrom(files("$rootDir/config/detekt/detekt.yml"))
    buildUponDefaultConfig.set(true)
    ignoreFailures.set(false)
}

// NamedArguments implements RequiresAnalysisApi, so it only reports when detekt
// runs with a compile classpath. The plain `detekt` task has no classpath and
// would silently pass, hence the analysis aware tasks are wired into `check`
// and the plain one is disabled.
tasks.named("detekt") {
    enabled = false
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    jvmTarget.set(JvmTarget.JVM_21.target)
    reports {
        html.required.set(true)
        checkstyle.required.set(true)
        sarif.required.set(false)
        markdown.required.set(false)
    }
}

tasks.named("check") {
    dependsOn("detektMain", "detektTest")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs = listOf("-Xjsr305=strict")
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
