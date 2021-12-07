import kotlinx.kover.api.CoverageEngine.JACOCO

plugins {
    kotlin("multiplatform") version "1.6.0"
    id("org.jetbrains.kotlinx.kover") version "0.4.2"
    id("org.jetbrains.dokka") version "1.6.0"
}

group = "net.swiftzer.semver"
version = "2.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js {
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
            }
        }
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

kover {
    isEnabled = true
    coverageEngine.set(JACOCO)
    jacocoEngineVersion.set("0.8.7")
    generateReportOnCheck.set(true)
}
