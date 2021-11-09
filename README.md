# SemVer

[![Build Status](https://travis-ci.org/swiftzer/semver.svg?branch=master)](https://travis-ci.org/swiftzer/semver)
[![codecov](https://codecov.io/gh/swiftzer/semver/branch/master/graph/badge.svg)](https://codecov.io/gh/swiftzer/semver)

Kotlin data class for [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html) specification.

Support parsing version number string and comparing version numbers using `Comparable` interface.

## Installation

Gradle

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'net.swiftzer.semver:semver:1.1.2'
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
assertEquals(-1, semVer1.compareTo(semVer2))
```
