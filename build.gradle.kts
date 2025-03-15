@file:OptIn(ExperimentalWasmDsl::class)

import com.vanniktech.maven.publish.SonatypeHost
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.binaryCompatibilityValidator)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
    alias(libs.plugins.burst)
    alias(libs.plugins.gradleMavenPublish)
}

val publishingPropertiesFile: File = rootProject.file("publishing.properties")
val publishingProperties = Properties()
if (publishingPropertiesFile.exists()) {
    publishingProperties.load(FileInputStream(publishingPropertiesFile))
}

kotlin {
    explicitApi()

    jvm {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    wasmJs {
        nodejs()
        binaries.executable()
    }

    mingwX64()

    linuxX64()
    linuxArm64()

    macosX64()
    macosArm64()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    watchosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()

    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()

    watchosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosDeviceArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotest.assertionsCore)
            implementation(libs.kotlinx.serialization.json)
        }
        jvmTest.dependencies {
            implementation(libs.kotlin.test.junit5)
        }
    }
}

dokka {
    dokkaSourceSets.configureEach {
        sourceLink {
            val relPath = rootProject.projectDir.toPath().relativize(projectDir.toPath())
            localDirectory.set(project.file("src"))
            remoteUrl("https://github.com/swiftzer/semver/tree/main/$relPath/src")
            remoteLineSuffix.set("#L")
        }
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
    coordinates(
        groupId = "net.swiftzer.semver",
        artifactId = "semver",
        version = buildString {
            append("2.1.0")
            val suffix = getProperty("versionSuffix")
            if (suffix != null) {
                append("-")
                append(suffix)
            }
        },
    )
    pom {
        name.set("SemVer")
        description.set("Kotlin data class for Semantic Versioning 2.0.0")
        inceptionYear.set("2017")
        url.set("https://github.com/swiftzer/semver")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("ericksli")
                name.set("Eric Li")
                email.set("eric@swiftzer.net")
            }
        }
        scm {
            url.set("https://github.com/swiftzer/semver/tree/main")
            connection.set("scm:git:ssh://github.com/swiftzer/semver.git")
            developerConnection.set("scm:git:ssh://github.com/swiftzer/semver.git")
        }
    }
}

tasks.register("detektAll") {
    group = "verification"
    dependsOn(tasks.withType<Detekt>())
}

fun getProperty(propertyName: String): String? =
    providers.environmentVariable(propertyName).orNull ?: publishingProperties.getProperty(propertyName)
