plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.personal.finances"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jooq)
    implementation(libs.spring.boot.docker.compose)
    implementation(libs.liquibase.core)
    implementation(libs.postgresql)
    implementation(libs.jooq)
    implementation(libs.mapstruct)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)

    annotationProcessor(libs.mapstruct.processor)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.spring)
    testImplementation(libs.cucumber.junit.platform.engine)
}

dependencyManagement {
    imports {
        mavenBom(libs.testcontainers.bom.get().toString())
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
