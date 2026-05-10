import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "8.4.0"
    id("java-library")
    id("jacoco")
}

group = "com.salmonspark.cloudflare.turnstile"
// version '1.1.6-SNAPSHOT'
description = "SpringBoot Cloudflare Turnstile Library"

extra["springBootVersion"] = "4.0.6"
extra["lombokVersion"] = "1.18.46"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

dependencies {
    // Spring Boot dependencies
    compileOnly("org.springframework.boot:spring-boot-starter-web:${property("springBootVersion")}")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator:${property("springBootVersion")}")

    // Lombok dependencies
    compileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${property("springBootVersion")}")
    annotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")

    // Lombok dependencies for test classes
    testCompileOnly("org.projectlombok:lombok:${property("lombokVersion")}")
    testAnnotationProcessor("org.projectlombok:lombok:${property("lombokVersion")}")
    testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${property("springBootVersion")}")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-web:${property("springBootVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-test:${property("springBootVersion")}")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator:${property("springBootVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveBaseName.set("salmonspark-spring-cloudflare-turnstile")
    archiveClassifier.set("")
}

// Run tests with different JDK versions
tasks.register<Test>("testJdk17") {
    javaLauncher =
        javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(17)
        }
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform()
    doFirst {
        println("Running tests with JDK 17")
    }
}

tasks.register<Test>("testJdk21") {
    javaLauncher =
        javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(21)
        }
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform()
    doFirst {
        println("Running tests with JDK 21")
    }
}

// Task that runs both test tasks
tasks.register<Test>("testAll") {
    dependsOn(tasks.named("testJdk17"), tasks.named("testJdk21"))
}

// Ensure the default 'test' task triggers both test tasks
tasks.test {
    useJUnitPlatform()
    dependsOn(tasks.named("testAll"))
}

pluginManager.withPlugin("com.diffplug.spotless") {
    extensions.configure<SpotlessExtension> {
        encoding("UTF-8")
        java {
            palantirJavaFormat()
            importOrder()
            removeUnusedImports()
            formatAnnotations()
            trimTrailingWhitespace()
            endWithNewline()
            toggleOffOn()
        }

        kotlin {
            ktlint()
        }

        kotlinGradle {
            ktlint()
        }
    }
}
