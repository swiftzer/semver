package net.swiftzer.semver

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class SemVerParseSuccessTest : FunSpec({
    data class Param(
        val version: String,
        val expected: SemVer,
    )
    withData(
        nameFn = { it.version },
        ts = sequenceOf(
            Param(
                version = "0.0.4",
                expected = SemVer(major = 0, minor = 0, patch = 4),
            ),
            Param(
                version = "1.2.3",
                expected = SemVer(major = 1, minor = 2, patch = 3),
            ),
            Param(
                version = "10.20.30",
                expected = SemVer(major = 10, minor = 20, patch = 30),
            ),
            Param(
                version = "1.1.2-prerelease+meta",
                expected = SemVer(major = 1, minor = 1, patch = 2, preRelease = "prerelease", buildMetadata = "meta"),
            ),
            Param(
                version = "1.1.2+meta",
                expected = SemVer(major = 1, minor = 1, patch = 2, preRelease = null, buildMetadata = "meta"),
            ),
            Param(
                version = "1.1.2+meta-valid",
                expected = SemVer(major = 1, minor = 1, patch = 2, preRelease = null, buildMetadata = "meta-valid"),
            ),
            Param(
                version = "1.0.0-alpha",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha"),
            ),
            Param(
                version = "1.0.0-beta",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "beta"),
            ),
            Param(
                version = "1.0.0-alpha.beta",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha.beta"),
            ),
            Param(
                version = "1.0.0-alpha.beta.1",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha.beta.1"),
            ),
            Param(
                version = "1.0.0-alpha.1",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha.1"),
            ),
            Param(
                version = "1.0.0-alpha0.valid",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha0.valid"),
            ),
            Param(
                version = "1.0.0-alpha.0valid",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha.0valid"),
            ),
            Param(
                version = "1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay",
                expected = SemVer(
                    major = 1,
                    minor = 0,
                    patch = 0,
                    preRelease = "alpha-a.b-c-somethinglong",
                    buildMetadata = "build.1-aef.1-its-okay"
                ),
            ),
            Param(
                version = "1.0.0-rc.1+build.1",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "rc.1", buildMetadata = "build.1"),
            ),
            Param(
                version = "2.0.0-rc.1+build.123",
                expected = SemVer(major = 2, minor = 0, patch = 0, preRelease = "rc.1", buildMetadata = "build.123"),
            ),
            Param(
                version = "1.2.3-beta",
                expected = SemVer(major = 1, minor = 2, patch = 3, preRelease = "beta"),
            ),
            Param(
                version = "10.2.3-DEV-SNAPSHOT",
                expected = SemVer(major = 10, minor = 2, patch = 3, preRelease = "DEV-SNAPSHOT"),
            ),
            Param(
                version = "1.2.3-SNAPSHOT-123",
                expected = SemVer(major = 1, minor = 2, patch = 3, preRelease = "SNAPSHOT-123"),
            ),
            Param(
                version = "1.0.0",
                expected = SemVer(major = 1, minor = 0, patch = 0),
            ),
            Param(
                version = "2.0.0",
                expected = SemVer(major = 2, minor = 0, patch = 0),
            ),
            Param(
                version = "1.1.7",
                expected = SemVer(major = 1, minor = 1, patch = 7),
            ),
            Param(
                version = "2.0.0+build.1848",
                expected = SemVer(major = 2, minor = 0, patch = 0, preRelease = null, buildMetadata = "build.1848"),
            ),
            Param(
                version = "2.0.1-alpha.1227",
                expected = SemVer(major = 2, minor = 0, patch = 1, preRelease = "alpha.1227"),
            ),
            Param(
                version = "1.0.0-alpha+beta",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha", buildMetadata = "beta"),
            ),
            Param(
                version = "1.2.3----RC-SNAPSHOT.12.9.1--.12+788",
                expected = SemVer(
                    major = 1,
                    minor = 2,
                    patch = 3,
                    preRelease = "---RC-SNAPSHOT.12.9.1--.12",
                    buildMetadata = "788"
                ),
            ),
            Param(
                version = "1.2.3----R-S.12.9.1--.12+meta",
                expected = SemVer(
                    major = 1,
                    minor = 2,
                    patch = 3,
                    preRelease = "---R-S.12.9.1--.12",
                    buildMetadata = "meta"
                ),
            ),
            Param(
                version = "1.2.3----RC-SNAPSHOT.12.9.1--.12",
                expected = SemVer(major = 1, minor = 2, patch = 3, preRelease = "---RC-SNAPSHOT.12.9.1--.12"),
            ),
            Param(
                version = "1.0.0+0.build.1-rc.10000aaa-kk-0.1",
                expected = SemVer(
                    major = 1,
                    minor = 0,
                    patch = 0,
                    preRelease = null,
                    buildMetadata = "0.build.1-rc.10000aaa-kk-0.1"
                ),
            ),
            Param(
                version = "1.0.0-0A.is.legal",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "0A.is.legal"),
            ),
        )
    ) {
        SemVer.parse(it.version) shouldBe it.expected
        SemVer.parseOrNull(it.version) shouldBe it.expected
    }
})
