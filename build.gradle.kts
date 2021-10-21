plugins {
    application
    kotlin("jvm")                              version "1.5.31"
    kotlin("plugin.spring")                    version "1.5.31"
    id("org.springframework.boot")             version "2.5.6"
    id("io.spring.dependency-management")      version "1.0.11.RELEASE"
    id("org.springframework.experimental.aot") version "0.10.2"
}

repositories {
    maven(uri("https://repo.spring.io/release"))
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.cloudevents:cloudevents-spring:2.2.0")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("io.cloudevents:cloudevents-http-basic:2.2.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
}

application {
    mainClass.set("ef.MainKt")
}

tasks.register<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImageNative") {
    dependsOn(tasks.bootJar)

    val args = setOf(
        "-Dspring.spel.ignore=true",
        "-Dspring.native.remove-yaml-support=true"
    )

    archiveFile.set(tasks.bootJar.get().archiveFile.get())
    builder = "paketobuildpacks/builder:tiny"
    environment = mapOf(
        "BP_NATIVE_IMAGE" to "1",
        "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to args.joinToString(" ")
    )
}
