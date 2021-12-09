import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.0"
}

group = "adventofcode"
version = "1.0"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        java.srcDir("src")
    }
    test {
        java.srcDir("test")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}