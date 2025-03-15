package net.swiftzer.semver

import app.cash.burst.Burst
import app.cash.burst.burstValues
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeNegative
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

@Burst
class SemVerTest {
    @Test
    fun initMajorValidation() {
        SemVer(major = 0).major shouldBe 0
        SemVer(major = 1).major shouldBe 1
        shouldThrow<IllegalArgumentException> { SemVer(major = -1) }
    }

    @Test
    fun initMinorValidation() {
        SemVer(major = 0, minor = 0).minor shouldBe 0
        SemVer(major = 0, minor = 1).minor shouldBe 1
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, minor = -1) }
    }

    @Test
    fun initPatchValidation() {
        SemVer(major = 0, patch = 0).patch shouldBe 0
        SemVer(major = 0, patch = 1).patch shouldBe 1
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, patch = -1) }
    }

    @Test
    fun initPreReleaseValidation() {
        SemVer(major = 0, preRelease = "a1B2c3").preRelease shouldBe "a1B2c3"
        SemVer(major = 0, preRelease = "0").preRelease shouldBe "0"
        SemVer(major = 0, preRelease = "01s").preRelease shouldBe "01s"
        SemVer(major = 0, preRelease = "1").preRelease shouldBe "1"
        SemVer(major = 0, preRelease = "1024").preRelease shouldBe "1024"
        SemVer(major = 0, preRelease = "--a1b2C3").preRelease shouldBe "--a1b2C3"

        shouldThrow<IllegalArgumentException> { SemVer(major = 0, preRelease = " ") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, preRelease = "a!bc") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, preRelease = "007") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, preRelease = " --a1b2C3") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, preRelease = "--a1b2C3 ") }
    }

    @Test
    fun initBuildMetadataValidation() {
        SemVer(major = 0, buildMetadata = "meta-valid").buildMetadata shouldBe "meta-valid"
        SemVer(major = 0, buildMetadata = "0").buildMetadata shouldBe "0"
        SemVer(major = 0, buildMetadata = "1").buildMetadata shouldBe "1"
        SemVer(
            major = 0, buildMetadata = "0.build.1-rc.10000aaa-kk-0.1",
        ).buildMetadata shouldBe "0.build.1-rc.10000aaa-kk-0.1"
        SemVer(major = 0, buildMetadata = "--a1b2C3").buildMetadata shouldBe "--a1b2C3"
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, buildMetadata = " ") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, buildMetadata = "a!bc") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, buildMetadata = "meta+meta") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, buildMetadata = "+meta") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, buildMetadata = " a1b2C3") }
        shouldThrow<IllegalArgumentException> { SemVer(major = 0, buildMetadata = "a1b2C3 ") }
    }

    @Test
    fun isInitialDevelopmentPhase() {
        SemVer(0, 1, 2).isInitialDevelopmentPhase().shouldBeTrue()
        SemVer(1, 2, 3).isInitialDevelopmentPhase().shouldBeFalse()
    }

    @Test
    fun nextMajor() {
        SemVer(major = 1, minor = 3, patch = 5, preRelease = "prerelease", buildMetadata = "meta")
            .nextMajor() shouldBe SemVer(major = 2, minor = 0, patch = 0)
    }

    @Test
    fun nextMinor() {
        SemVer(major = 1, minor = 3, patch = 5, preRelease = "prerelease", buildMetadata = "meta")
            .nextMinor() shouldBe SemVer(major = 1, minor = 4, patch = 0)
    }

    @Test
    fun nextPatch() {
        SemVer(major = 1, minor = 3, patch = 5, preRelease = "prerelease", buildMetadata = "meta")
            .nextPatch() shouldBe SemVer(major = 1, minor = 3, patch = 6)
    }

    @Test
    fun testToString() {
        SemVer(major = 0, minor = 11, patch = 222).toString() shouldBe "0.11.222"
        SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha.1").toString() shouldBe "1.0.0-alpha.1"
        SemVer(
            major = 1, minor = 1, patch = 2, preRelease = null, buildMetadata = "meta-valid",
        ).toString() shouldBe "1.1.2+meta-valid"
        SemVer(
            major = 2, minor = 0, patch = 0, preRelease = "rc.1", buildMetadata = "build.123",
        ).toString() shouldBe "2.0.0-rc.1+build.123"
    }

    @Test
    fun compareToMajor() {
        val smaller = SemVer(major = 1)
        val larger = SemVer(major = 2)
        smaller.compareTo(larger).shouldBeNegative()
        larger.compareTo(smaller).shouldBePositive()
        smaller.compareTo(smaller) shouldBe 0
    }

    @Test
    fun compareToMinor() {
        val smaller = SemVer(major = 1, minor = 1)
        val larger = SemVer(major = 1, minor = 2)
        smaller.compareTo(larger).shouldBeNegative()
        larger.compareTo(smaller).shouldBePositive()
        smaller.compareTo(smaller) shouldBe 0
    }

    @Test
    fun compareToPatch() {
        val smaller = SemVer(major = 1, minor = 1, patch = 1)
        val larger = SemVer(major = 1, minor = 1, patch = 2)
        smaller.compareTo(larger).shouldBeNegative()
        larger.compareTo(smaller).shouldBePositive()
        smaller.compareTo(smaller) shouldBe 0
    }

    @Test
    fun compareToRelease() {
        val version1 = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha")
        val version2 = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha.1")
        val version3 = SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha.beta")
        val version4 = SemVer(major = 1, minor = 0, patch = 0, preRelease = "beta")
        val version5 = SemVer(major = 1, minor = 0, patch = 0, preRelease = "beta.2")
        val version6 = SemVer(major = 1, minor = 0, patch = 0, preRelease = "beta.11")
        val version7 = SemVer(major = 1, minor = 0, patch = 0, preRelease = "rc.1")
        val version8 = SemVer(major = 1, minor = 0, patch = 0)

        version1.compareTo(version2).shouldBeNegative()
        version2.compareTo(version3).shouldBeNegative()
        version3.compareTo(version4).shouldBeNegative()
        version4.compareTo(version5).shouldBeNegative()
        version5.compareTo(version6).shouldBeNegative()
        version6.compareTo(version7).shouldBeNegative()
        version7.compareTo(version8).shouldBeNegative()
        version1.compareTo(version8).shouldBeNegative()

        version2.compareTo(version1).shouldBePositive()
        version3.compareTo(version2).shouldBePositive()
        version4.compareTo(version3).shouldBePositive()
        version5.compareTo(version4).shouldBePositive()
        version6.compareTo(version5).shouldBePositive()
        version7.compareTo(version6).shouldBePositive()
        version8.compareTo(version7).shouldBePositive()
        version8.compareTo(version1).shouldBePositive()

        version6.compareTo(version6) shouldBe 0
    }

    @Test
    fun comparePreReleaseExceedLongRange() {
        val smaller = SemVer(1, 0, 0, "111.99999999999999999999998")
        val larger = SemVer(1, 0, 0, "111.99999999999999999999999")
        smaller.compareTo(larger).shouldBeNegative()
        larger.compareTo(smaller).shouldBePositive()
    }

    @Test
    fun parseSuccess(
        param: Param = burstValues(
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
                expected = SemVer(
                    major = 1,
                    minor = 1,
                    patch = 2,
                    preRelease = "prerelease",
                    buildMetadata = "meta",
                ),
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
                    buildMetadata = "build.1-aef.1-its-okay",
                ),
            ),
            Param(
                version = "1.0.0-rc.1+build.1",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "rc.1", buildMetadata = "build.1"),
            ),
            Param(
                version = "2.0.0-rc.1+build.123",
                expected = SemVer(
                    major = 2,
                    minor = 0,
                    patch = 0,
                    preRelease = "rc.1",
                    buildMetadata = "build.123",
                ),
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
                    buildMetadata = "788",
                ),
            ),
            Param(
                version = "1.2.3----R-S.12.9.1--.12+meta",
                expected = SemVer(
                    major = 1,
                    minor = 2,
                    patch = 3,
                    preRelease = "---R-S.12.9.1--.12",
                    buildMetadata = "meta",
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
                    buildMetadata = "0.build.1-rc.10000aaa-kk-0.1",
                ),
            ),
            Param(
                version = "1.0.0-0A.is.legal",
                expected = SemVer(major = 1, minor = 0, patch = 0, preRelease = "0A.is.legal"),
            ),
        )
    ) {
        SemVer.parse(param.version) shouldBe param.expected
        SemVer.parseOrNull(param.version) shouldBe param.expected
    }

    @Suppress("MaxLineLength")
    @Test
    fun parseFail(
        version: String = burstValues(
            "v1.2.3",
            " 1.2.3",
            "1.2.3 ",
            "1",
            "a",
            "1.2",
            "1.2.3-0123",
            "1.2.3-0123.0123",
            "1.1.2+.123",
            "+invalid",
            "-invalid",
            "-invalid+invalid",
            "-invalid.01",
            "alpha",
            "alpha.beta",
            "alpha.beta.1",
            "alpha.1",
            "alpha+beta",
            "alpha_beta",
            "alpha.",
            "alpha..",
            "beta",
            "1.0.0-alpha_beta",
            "-alpha.",
            "1.0.0-alpha..",
            "1.0.0-alpha..1",
            "1.0.0-alpha...1",
            "1.0.0-alpha....1",
            "1.0.0-alpha.....1",
            "1.0.0-alpha......1",
            "1.0.0-alpha.......1",
            "01.1.1",
            "1.01.1",
            "1.1.01",
            "1.2.3.DEV",
            "1.2-SNAPSHOT",
            "1.2.31.2.3----RC-SNAPSHOT.12.09.1--..12+788",
            "1.2-RC-SNAPSHOT",
            "-1.0.3-gamma+b7718",
            "+justmeta",
            "9.8.7+meta+meta",
            "9.8.7-whatever+meta+meta",
            "99999999999999999999999.999999999999999999.99999999999999999----RC-SNAPSHOT.12.09.1--------------------------------..12",
        )
    ) {
        shouldThrow<IllegalArgumentException> { SemVer.parse(version) }
        SemVer.parseOrNull(version).shouldBeNull()
    }

    @Test
    fun parseExceedIntRange() {
        // The type for major/minor/patch are Long, thus throw exception
        shouldThrow<NumberFormatException> {
            SemVer.parse("99999999999999999999999.999999999999999999.99999999999999999")
        }
    }

    @Test
    fun parseOrNullExceedIntRange() {
        // The type for major/minor/patch are Long, thus throw exception
        SemVer.parseOrNull("99999999999999999999999.999999999999999999.99999999999999999").shouldBeNull()
    }

    data class Param(
        val version: String,
        val expected: SemVer,
    )
}
