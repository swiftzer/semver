import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotest.multiplatform)
    alias(libs.plugins.kotlinx.binaryCompatibilityValidator)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
    id("maven-publish")
    id("signing")
}

group = "net.swiftzer.semver"
version = "2.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    js(IR) {
        browser()
        nodejs()
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotest.frameworkEngine)
                implementation(libs.kotest.frameworkDatatest)
                implementation(libs.kotest.assertionsCore)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.runnerJunit5)
            }
        }
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            sourceLink {
                val relPath = rootProject.projectDir.toPath().relativize(projectDir.toPath())
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URL("https://github.com/swiftzer/semver/tree/multiplatform/${relPath}/src"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}

extensions.configure<PublishingExtension> {
    repositories {
        maven {
            val isSnapshot = version.toString().endsWith("SNAPSHOT")
            url = uri(
                if (isSnapshot) {
                    "https://oss.sonatype.org/content/repositories/snapshots"
                } else {
                    "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                }
            )
            credentials {
                username = providers.gradleProperty("ossrhUsername").get()
                password = providers.gradleProperty("ossrhPassword").get()
            }
        }

        val javadocJar = tasks.register<Jar>("javadocJar") {
            dependsOn(tasks.dokkaHtml)
            archiveClassifier.set("javadoc")
            from("$buildDir/dokka")
        }

        publications {
            withType<MavenPublication> {
                artifact(javadocJar)
                pom {
                    name.set("SemVer")
                    description.set("Kotlin data class for Semantic Versioning 2.0.0")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/mit-license.php")
                        }
                    }
                    url.set("https://github.com/swiftzer/semver")
                    issueManagement {
                        system.set("GitHub")
                        url.set("https://github.com/swiftzer/semver/issues")
                    }
                    scm {
                        connection.set("scm:git:github.com/swiftzer/semver.git")
                        developerConnection.set("scm:git:ssh://github.com/swiftzer/semver.git")
                        url.set("https://github.com/swiftzer/semver/tree/main")
                    }
                    developers {
                        developer {
                            id.set("ericksli")
                            name.set("Eric Li")
                            email.set("eric@swiftzer.net")
                        }
                    }
                }
            }
        }

        val publishing = extensions.getByType<PublishingExtension>()
        extensions.configure<SigningExtension> {
            useInMemoryPgpKeys(
                providers.gradleProperty("signingKey").get(),
                providers.gradleProperty("signingPassword").get(),
            )
            sign(publishing.publications)
        }
        project.tasks.withType(AbstractPublishToMaven::class.java).configureEach {
            dependsOn(project.tasks.withType(Sign::class.java))
        }
    }
}