# SemVer

[![codecov](https://codecov.io/gh/swiftzer/semver/graph/badge.svg?token=iJ3CY95nl6)](https://codecov.io/gh/swiftzer/semver)

Kotlin data class for [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html) specification with
Kotlin [Multiplatform](https://kotlinlang.org/docs/multiplatform-get-started.html)
and [Serialization](https://kotlinlang.org/docs/serialization.html) support.

Support parsing version number string and comparing version numbers using `Comparable` interface.

## Installation

Gradle

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("net.swiftzer.semver:semver:2.0.0")
}
```

## Usage

Parsing version number

```kotlin
val version: SemVer = SemVer.parse("1.0.0-beta+exp.sha.5114f85")

version.major // 1
version.minor // 0
version.patch // 0
version.preRelease // "beta"
version.buildMetadata // "exp.sha.5114f85"
```

Comparing version numbers

```kotlin
val semVer1 = SemVer(1, 0, 0)
val semVer2 = SemVer(1, 0, 2)
assertTrue(semVer1 < semVer2)
```

Creating next version numbers

```kotlin
val semVer = SemVer(1, 3, 5)
assertEquals(SemVer(1, 3, 6), semVer.nextPatch())
assertEquals(SemVer(1, 4, 0), semVer.nextMinor())
assertEquals(SemVer(2, 0, 0), semVer.nextMajor())
```
