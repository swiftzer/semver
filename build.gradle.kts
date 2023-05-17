import kotlinx.kover.api.CoverageEngine.JACOCO

plugins {
    kotlin("multiplatform") version "1.7.20"
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("org.jetbrains.dokka") version "1.7.10"
    id("io.kotest.multiplatform") version "5.0.2"
}

group = "net.swiftzer.semver"
version = "2.0.0"

ext["kotestVersion"] = "5.5"

repositories {
    mavenCentral()
}

kotlin {
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
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.kotest:kotest-framework-engine:${ext["kotestVersion"]}")
                implementation("io.kotest:kotest-assertions-core:${ext["kotestVersion"]}")
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:${ext["kotestVersion"]}")
            }
        }
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}
//
//kover {
////    isEnabled = true
//    coverageEngine.set(JACOCO)
//    jacocoEngineVersion.set("0.8.7")
//    generateReportOnCheck.set(true)
//}

//extensions.configure<KoverMergedConfig> {
//    enable()
//    // configure merged tasks
//}

//koverMerged {
//    enable()
//}