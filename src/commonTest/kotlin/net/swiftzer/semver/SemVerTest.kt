package net.swiftzer.semver

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeNegative
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class SemVerTest : FunSpec(
    {
        test("init major validation") {
            SemVer(major = 0).major shouldBe 0
            SemVer(major = 1).major shouldBe 1
            shouldThrow<IllegalArgumentException> { SemVer(major = -1) }
        }

        test("init minor validation") {
            SemVer(major = 0, minor = 0).minor shouldBe 0
            SemVer(major = 0, minor = 1).minor shouldBe 1
            shouldThrow<IllegalArgumentException> { SemVer(major = 0, minor = -1) }
        }

        test("init patch validation") {
            SemVer(major = 0, patch = 0).patch shouldBe 0
            SemVer(major = 0, patch = 1).patch shouldBe 1
            shouldThrow<IllegalArgumentException> { SemVer(major = 0, patch = -1) }
        }

        test("init preRelease validation") {
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

        test("init buildMetadata validation") {
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

        test("isInitialDevelopmentPhase") {
            SemVer(0, 1, 2).isInitialDevelopmentPhase().shouldBeTrue()
            SemVer(1, 2, 3).isInitialDevelopmentPhase().shouldBeFalse()
        }

        test("nextMajor") {
            SemVer(
                major = 1,
                minor = 3,
                patch = 5,
                preRelease = "prerelease",
                buildMetadata = "meta",
            ).nextMajor() shouldBe SemVer(major = 2, minor = 0, patch = 0)
        }

        test("nextMinor") {
            SemVer(
                major = 1,
                minor = 3,
                patch = 5,
                preRelease = "prerelease",
                buildMetadata = "meta",
            ).nextMinor() shouldBe SemVer(major = 1, minor = 4, patch = 0)
        }

        test("nextPatch") {
            SemVer(
                major = 1,
                minor = 3,
                patch = 5,
                preRelease = "prerelease",
                buildMetadata = "meta",
            ).nextPatch() shouldBe SemVer(major = 1, minor = 3, patch = 6)
        }

        test("toString") {
            SemVer(major = 0, minor = 11, patch = 222).toString() shouldBe "0.11.222"
            SemVer(major = 1, minor = 0, patch = 0, preRelease = "alpha.1").toString() shouldBe "1.0.0-alpha.1"
            SemVer(
                major = 1, minor = 1, patch = 2, preRelease = null, buildMetadata = "meta-valid",
            ).toString() shouldBe "1.1.2+meta-valid"
            SemVer(
                major = 2, minor = 0, patch = 0, preRelease = "rc.1", buildMetadata = "build.123",
            ).toString() shouldBe "2.0.0-rc.1+build.123"
        }

        test("compareTo major") {
            val smaller = SemVer(major = 1)
            val larger = SemVer(major = 2)
            smaller.compareTo(larger).shouldBeNegative()
            larger.compareTo(smaller).shouldBePositive()
            smaller.compareTo(smaller) shouldBe 0
        }

        test("compareTo minor") {
            val smaller = SemVer(major = 1, minor = 1)
            val larger = SemVer(major = 1, minor = 2)
            smaller.compareTo(larger).shouldBeNegative()
            larger.compareTo(smaller).shouldBePositive()
            smaller.compareTo(smaller) shouldBe 0
        }

        test("compareTo patch") {
            val smaller = SemVer(major = 1, minor = 1, patch = 1)
            val larger = SemVer(major = 1, minor = 1, patch = 2)
            smaller.compareTo(larger).shouldBeNegative()
            larger.compareTo(smaller).shouldBePositive()
            smaller.compareTo(smaller) shouldBe 0
        }

        test("compareTo release") {
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

        test("compare preRelease exceed Long range") {
            val smaller = SemVer(1, 0, 0, "111.99999999999999999999998")
            val larger = SemVer(1, 0, 0, "111.99999999999999999999999")
            smaller.compareTo(larger).shouldBeNegative()
            larger.compareTo(smaller).shouldBePositive()
        }

        test("parse exceed Int range") {
            // The type for major/minor/patch are Long, thus throw exception
            shouldThrow<NumberFormatException> {
                SemVer.parse("99999999999999999999999.999999999999999999.99999999999999999")
            }
        }

        test("parseOrNull exceed Int range") {
            // The type for major/minor/patch are Long, thus throw exception
            SemVer.parseOrNull("99999999999999999999999.999999999999999999.99999999999999999").shouldBeNull()
        }
    },
)
