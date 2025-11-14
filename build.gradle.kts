plugins {
    id("java")
    id("war")
    id("io.openliberty.tools.gradle.Liberty") version "3.9.5"
    id("io.freefair.lombok") version "8.6"
}

group = "se.ifmo"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    providedCompile("jakarta.platform:jakarta.jakartaee-api:10.0.0")
    providedCompile("jakarta.enterprise:jakarta.enterprise.cdi-api:4.0.1")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("commons-codec:commons-codec:1.18.0")
}

ext {
    liberty.server.`var`["http_port"] = "9080"
    liberty.server.`var`["https_port"] = "9443"
    liberty.server.`var`["app.context.root"] = project.name
}

val copyPostgresDriver by tasks.registering(Copy::class) {
    from(configurations.runtimeClasspath) {
        include("postgresql-*.jar")
    }
    into("build/wlp/usr/shared/resources")

    doFirst {
        println("Copying PostgreSQL driver to: build/wlp/usr/shared/resources")
    }
}

tasks.named("deploy") {
    dependsOn(copyPostgresDriver)
}

tasks.named("libertyDev") {
    dependsOn(copyPostgresDriver)
}