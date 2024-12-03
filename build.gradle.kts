/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.1.0"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.io.quarkus.quarkus.resteasy)
    api(libs.io.quarkus.quarkus.resteasy.jackson)
    api(libs.io.quarkus.quarkus.smallrye.openapi)
    api(libs.ai.timefold.solver.timefold.solver.quarkus)
    api(libs.ai.timefold.solver.timefold.solver.quarkus.jackson)
    api(libs.io.quarkus.quarkus.webjars.locator)
    runtimeOnly(libs.ai.timefold.solver.timefold.solver.webui)
    runtimeOnly(libs.org.webjars.bootstrap)
    runtimeOnly(libs.org.webjars.jquery)
    runtimeOnly(libs.org.webjars.font.awesome)
    runtimeOnly(libs.org.webjars.npm.js.joda)
    testImplementation(libs.ai.timefold.solver.timefold.solver.test)
    testImplementation(libs.io.quarkus.quarkus.junit5)
    testImplementation(libs.io.rest.assured.rest.assured)
    testImplementation(libs.org.awaitility.awaitility)
    testImplementation(libs.org.assertj.assertj.core)
}

group = "org.acme"
version = "1.0-SNAPSHOT"
description = "maintenance-scheduling"
java.sourceCompatibility = JavaVersion.VERSION_16

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}