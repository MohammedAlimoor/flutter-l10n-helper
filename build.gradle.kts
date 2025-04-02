plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.yourdomain.flutterl10nhelper"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.json:json:20210307")
}

// Configure Java home explicitly
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Configure Gradle IntelliJ Plugin
intellij {
    version.set("2023.3.6") // Use a stable IntelliJ version
    type.set("IC") // IntelliJ Community Edition

    // No Dart or Flutter dependencies needed!
    plugins.set(listOf(
        "com.intellij.java"
    ))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    // Add this line to disable instrumentCode task if it's causing problems
    instrumentCode {
//        compilerVersion.set("211.7628.21")
        enabled = false
    }

    patchPluginXml {
        sinceBuild.set("233") // IntelliJ 2023.3+
        untilBuild.set("241.*") // Up to 2024.1.*
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}