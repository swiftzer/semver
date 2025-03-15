@file:OptIn(ExperimentalWasmDsl::class)

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
    `maven-publish`
    signing
}

val publishingPropertiesFile: File = rootProject.file("publishing.properties")
val publishingProperties = Properties()
if (publishingPropertiesFile.exists()) {
    publishingProperties.load(FileInputStream(publishingPropertiesFile))
}

group = "net.swiftzer.semver"
version = buildString {
    append("2.0.0")
    val suffix = getProperty("versionSuffix")
    if (suffix != null) {
        append("-")
        append(suffix)
    }
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

publishing {
    repositories {
        maven {
            name = "staging"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = getProperty("ossrhUsername")
                password = getProperty("ossrhPassword")
            }
        }
        maven {
            name = "snapshot"
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
            credentials {
                username = getProperty("ossrhUsername")
                password = getProperty("ossrhPassword")
            }
        }

        val javadocJar = tasks.register<Jar>("javadocJar") {
            dependsOn(tasks.dokkaGenerate)
            archiveClassifier = "javadoc"
            from(layout.buildDirectory.file("dokka"))
        }

        publications {
            withType<MavenPublication> {
                artifact(javadocJar)
                pom {
                    name = "SemVer"
                    description = "Kotlin data class for Semantic Versioning 2.0.0"
                    licenses {
                        license {
                            name = "MIT License"
                            url = "https://opensource.org/licenses/mit-license.php"
                        }
                    }
                    url = "https://github.com/swiftzer/semver"
                    issueManagement {
                        system = "GitHub"
                        url = "https://github.com/swiftzer/semver/issues"
                    }
                    scm {
                        connection = "scm:git:github.com/swiftzer/semver.git"
                        developerConnection = "scm:git:ssh://github.com/swiftzer/semver.git"
                        url = "https://github.com/swiftzer/semver/tree/main"
                    }
                    developers {
                        developer {
                            id = "ericksli"
                            name = "Eric Li"
                            email = "eric@swiftzer.net"
                        }
                    }
                }
            }
        }

        signing {
            useInMemoryPgpKeys(
                getProperty("signingKey"),
                getProperty("signingPassword"),
            )
            sign(publishing.publications)
        }
        project.tasks.withType<AbstractPublishToMaven>().configureEach {
            dependsOn(project.tasks.withType<Sign>())
        }
    }
}

tasks.register("detektAll") {
    group = "verification"
    dependsOn(tasks.withType<Detekt>())
}

fun getProperty(propertyName: String): String? =
    providers.environmentVariable(propertyName).orNull ?: publishingProperties.getProperty(propertyName)
