import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kapt)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.jooq.codegen)
}

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
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.jooq)
    implementation(libs.spring.boot.docker.compose)
    implementation(libs.spring.boot.starter.liquibase)
    implementation(libs.postgresql)
    implementation(libs.jooq)
    implementation(libs.mapstruct)
    implementation(libs.uuid.creator)
    implementation(libs.kotlin.reflect)
    implementation(libs.jackson.module.kotlin)

    kapt(libs.mapstruct.processor)

    runtimeOnly(libs.postgresql)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.junit.platform.suite)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.spring)
    testImplementation(libs.cucumber.junit.platform.engine)

    // JOOQ code generation dependencies
    jooqCodegen(libs.jooq.meta.extensions.liquibase)
    jooqCodegen(libs.liquibase.core)
    jooqCodegen(libs.postgresql)
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

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
    }
    includeCompileClasspath = false
}

tasks.matching { it.name == "kaptTestKotlin" }.configureEach {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// JOOQ code generation configuration
jooq {
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.extensions.liquibase.LiquibaseDatabase"
                properties {
                    property {
                        key = "rootPath"
                        value = "${projectDir}/src/main/resources"
                    }
                    property {
                        key = "scripts"
                        value = "db/changelog/db.changelog-master.yaml"
                    }
                }
            }
            generate {
                isDeprecated = false
                isRecords = true
                isPojos = false
                isFluentSetters = true
                isKotlinNotNullRecordAttributes = true
                isKotlinNotNullPojoAttributes = true
            }
            target {
                packageName = "com.jorgemonteiro.apps.finance.data.jooq"
                directory = "${layout.buildDirectory.get()}/generated/jooq"
            }
        }
    }
}

// Add generated sources to the compile classpath
sourceSets {
    main {
        kotlin {
            srcDir(layout.buildDirectory.dir("generated/jooq"))
        }
    }
}

// Ensure JOOQ codegen runs before compilation and kapt
tasks.named("compileKotlin") {
    dependsOn(tasks.named("jooqCodegen"))
}

tasks.withType<KaptGenerateStubsTask>().configureEach {
    dependsOn(tasks.named("jooqCodegen"))
}
