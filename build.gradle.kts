import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm").version("1.3.72")
    kotlin("plugin.serialization").version("1.3.72")
    jacoco
    application
    id("com.github.johnrengelman.shadow").version("5.2.0")
    id("com.diffplug.gradle.spotless") version "3.28.1"
}

repositories {
    jcenter()
    maven("https://jitpack.io")
    maven("https://dl.bintray.com/mipt-npm/dataforge")
    maven("https://dl.bintray.com/mipt-npm/kscience")
    mavenCentral()
}

configurations {
    this.all {
        exclude(group = "ch.qos.logback")
    }
}

application {
    mainClassName = "no.trulsjor.starter.ApplicationKt"
}

val junitVersion = "5.6.1"
val ktorVersion = "1.3.2"
val log4jVersion = "2.13.3"
val serializationVersion = "0.20.0"
val tikaVersion = "1.25"
val assertJVersion = "3.18.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$serializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    implementation("com.vlkan.log4j2:log4j2-logstash-layout-fatjar:0.19")
    implementation("org.apache.tika:tika-parsers:$tikaVersion")
    implementation("kscience.plotlykt:plotlykt-core:0.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI", "-Xuse-experimental=io.ktor.util.KtorExperimentalAPI", "-Xuse-experimental=kotlinx.serialization.UnstableDefault")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events("passed", "skipped", "failed")
    }
}

tasks.named("shadowJar") {
    dependsOn("test")
}

tasks.named("jar") {
    dependsOn("test")
}

tasks.named("spotlessCheck") {
    dependsOn("spotlessApply")
}

tasks.named("compileKotlin") {
    dependsOn("spotlessCheck")
}
spotless {
    kotlin {
        ktlint("0.35.0")
    }
    kotlinGradle {
        target("*.gradle.kts", "buildSrc/**/*.kt*")
        ktlint("0.35.0")
    }
}
