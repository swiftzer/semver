package semver

import net.swiftzer.semver.SemVer
import kotlin.test.*

class SemVerTest {
    @Test
    fun initMajorValidation() {
        assertEquals(0, SemVer(major = 0).major)
        assertEquals(1, SemVer(major = 1).major)
        assertFailsWith<IllegalArgumentException> { SemVer(major = -1) }
    }

    @Test
    fun initMinorValidation() {
        assertEquals(0, SemVer(minor = 0).minor)
        assertEquals(1, SemVer(minor = 1).minor)
        assertFailsWith<IllegalArgumentException> { SemVer(minor = -1) }
    }

    @Test
    fun initPatchValidation() {
        assertEquals(0, SemVer(patch = 0).patch)
        assertEquals(1, SemVer(patch = 1).patch)
        assertFailsWith<IllegalArgumentException> { SemVer(patch = -1) }
    }

    @Test
    fun initPreReleaseValidation() {
//        assertEquals("a1B2c3", SemVer(preRelease = "a1B2c3").preRelease)
//        assertEquals("0", SemVer(preRelease = "0").preRelease)
        assertEquals("01s", SemVer(preRelease = "01s").preRelease)
//        assertEquals("1", SemVer(preRelease = "1").preRelease)
//        assertEquals("1024", SemVer(preRelease = "1024").preRelease)
//        assertEquals("--a1b2C3", SemVer(preRelease = "--a1b2C3").preRelease)
//        assertFailsWith<IllegalArgumentException> { SemVer(preRelease = " ") }
//        assertFailsWith<IllegalArgumentException> { SemVer(preRelease = "a!bc") }
//        assertFailsWith<IllegalArgumentException> { SemVer(preRelease = "007") }
//        assertFailsWith<IllegalArgumentException> { SemVer(preRelease = " --a1b2C3") }
//        assertFailsWith<IllegalArgumentException> { SemVer(preRelease = "--a1b2C3 ") }
    }

    @Test
    fun initBuildMetadataValidation() {
        assertEquals("meta-valid", SemVer(buildMetadata = "meta-valid").buildMetadata)
        assertEquals("0", SemVer(buildMetadata = "0").buildMetadata)
        assertEquals("1", SemVer(buildMetadata = "1").buildMetadata)
        assertEquals(
            "0.build.1-rc.10000aaa-kk-0.1",
            SemVer(buildMetadata = "0.build.1-rc.10000aaa-kk-0.1").buildMetadata
        )
        assertEquals("--a1b2C3", SemVer(buildMetadata = "--a1b2C3").buildMetadata)
        assertFailsWith<IllegalArgumentException> { SemVer(buildMetadata = " ") }
        assertFailsWith<IllegalArgumentException> { SemVer(buildMetadata = "a!bc") }
        assertFailsWith<IllegalArgumentException> { SemVer(buildMetadata = "meta+meta") }
        assertFailsWith<IllegalArgumentException> { SemVer(buildMetadata = "+meta") }
        assertFailsWith<IllegalArgumentException> { SemVer(buildMetadata = " a1b2C3") }
        assertFailsWith<IllegalArgumentException> { SemVer(buildMetadata = "a1b2C3 ") }
    }

    @Test
    fun isInitialDevelopmentPhase() {
        assertTrue { SemVer(0, 1, 2).isInitialDevelopmentPhase() }
        assertFalse { SemVer(1, 2, 3).isInitialDevelopmentPhase() }
    }

    @Test
    fun customToString() {
        assertEquals("0.11.222", SemVer(0, 11, 222).toString())
        assertEquals("1.0.0-alpha.1", SemVer(1, 0, 0, "alpha.1").toString())
        assertEquals("1.1.2+meta-valid", SemVer(1, 1, 2, null, "meta-valid").toString())
        assertEquals("2.0.0-rc.1+build.123", SemVer(2, 0, 0, "rc.1", "build.123").toString())
    }

    @Test
    fun compareToMajor() {
        val smaller = SemVer(1)
        val larger = SemVer(2)
        assertTrue { smaller < larger }
        assertTrue { larger > smaller }
        assertEquals(0, smaller.compareTo(smaller))
    }

    @Test
    fun compareToMinor() {
        val smaller = SemVer(1, 1)
        val larger = SemVer(1, 2)
        assertTrue { smaller < larger }
        assertTrue { larger > smaller }
        assertEquals(0, smaller.compareTo(smaller))
    }

    @Test
    fun compareToPatch() {
        val smaller = SemVer(1, 1, 1)
        val larger = SemVer(1, 1, 2)
        assertTrue { smaller < larger }
        assertTrue { larger > smaller }
        assertEquals(0, smaller.compareTo(smaller))
    }

    @Test
    fun comparePreRelease() {
        val version1 = SemVer(1, 0, 0, "alpha")
        val version2 = SemVer(1, 0, 0, "alpha.1")
        val version3 = SemVer(1, 0, 0, "alpha.beta")
        val version4 = SemVer(1, 0, 0, "beta")
        val version5 = SemVer(1, 0, 0, "beta.2")
        val version6 = SemVer(1, 0, 0, "beta.11")
        val version7 = SemVer(1, 0, 0, "rc.1")
        val version8 = SemVer(1, 0, 0)

        assertTrue { version1 < version2 }
        assertTrue { version2 < version3 }
        assertTrue { version3 < version4 }
        assertTrue { version4 < version5 }
        assertTrue { version5 < version6 }
        assertTrue { version6 < version7 }
        assertTrue { version7 < version8 }
        assertTrue { version1 < version8 }

        assertTrue { version2 > version1 }
        assertTrue { version3 > version2 }
        assertTrue { version4 > version3 }
        assertTrue { version5 > version4 }
        assertTrue { version6 > version5 }
        assertTrue { version7 > version6 }
        assertTrue { version8 > version7 }
        assertTrue { version8 > version1 }

        assertEquals(0,version6.compareTo(version6) )
    }

    @Test
    fun comparePreReleaseExceedLongRange() {
        val smaller = SemVer(1, 0, 0,"111.99999999999999999999998")
        val larger = SemVer(1, 0, 0,"111.99999999999999999999999")
        assertTrue { smaller < larger }
        assertTrue { larger > smaller }
    }

    /**
     * Cases from the [suggested regular expression](https://regex101.com/r/vkijKf/1/).
     */
    @Test
    fun parseValid() {
        assertEquals(SemVer(0, 0, 4), SemVer.parse("0.0.4"))
        assertEquals(SemVer(1, 2, 3), SemVer.parse("1.2.3"))
        assertEquals(SemVer(10, 20, 30), SemVer.parse("10.20.30"))
        assertEquals(SemVer(1, 1, 2, "prerelease", "meta"), SemVer.parse("1.1.2-prerelease+meta"))
        assertEquals(SemVer(1, 1, 2, null, "meta"), SemVer.parse("1.1.2+meta"))
        assertEquals(SemVer(1, 1, 2, null, "meta-valid"), SemVer.parse("1.1.2+meta-valid"))
        assertEquals(SemVer(1, 0, 0, "alpha"), SemVer.parse("1.0.0-alpha"))
        assertEquals(SemVer(1, 0, 0, "beta"), SemVer.parse("1.0.0-beta"))
        assertEquals(SemVer(1, 0, 0, "alpha.beta"), SemVer.parse("1.0.0-alpha.beta"))
        assertEquals(SemVer(1, 0, 0, "alpha.beta.1"), SemVer.parse("1.0.0-alpha.beta.1"))
        assertEquals(SemVer(1, 0, 0, "alpha.1"), SemVer.parse("1.0.0-alpha.1"))
        assertEquals(SemVer(1, 0, 0, "alpha0.valid"), SemVer.parse("1.0.0-alpha0.valid"))
        assertEquals(SemVer(1, 0, 0, "alpha.0valid"), SemVer.parse("1.0.0-alpha.0valid"))
        assertEquals(
            SemVer(1, 0, 0, "alpha-a.b-c-somethinglong", "build.1-aef.1-its-okay"),
            SemVer.parse("1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay")
        )
        assertEquals(SemVer(1, 0, 0, "rc.1", "build.1"), SemVer.parse("1.0.0-rc.1+build.1"))
        assertEquals(SemVer(2, 0, 0, "rc.1", "build.123"), SemVer.parse("2.0.0-rc.1+build.123"))
        assertEquals(SemVer(1, 2, 3, "beta"), SemVer.parse("1.2.3-beta"))
        assertEquals(SemVer(10, 2, 3, "DEV-SNAPSHOT"), SemVer.parse("10.2.3-DEV-SNAPSHOT"))
        assertEquals(SemVer(1, 2, 3, "SNAPSHOT-123"), SemVer.parse("1.2.3-SNAPSHOT-123"))
        assertEquals(SemVer(1, 0, 0), SemVer.parse("1.0.0"))
        assertEquals(SemVer(2, 0, 0), SemVer.parse("2.0.0"))
        assertEquals(SemVer(1, 1, 7), SemVer.parse("1.1.7"))
        assertEquals(SemVer(2, 0, 0, null, "build.1848"), SemVer.parse("2.0.0+build.1848"))
        assertEquals(SemVer(2, 0, 1, "alpha.1227"), SemVer.parse("2.0.1-alpha.1227"))
        assertEquals(SemVer(1, 0, 0, "alpha", "beta"), SemVer.parse("1.0.0-alpha+beta"))
        assertEquals(
            SemVer(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "788"),
            SemVer.parse("1.2.3----RC-SNAPSHOT.12.9.1--.12+788")
        )
        assertEquals(SemVer(1, 2, 3, "---R-S.12.9.1--.12", "meta"), SemVer.parse("1.2.3----R-S.12.9.1--.12+meta"))
        assertEquals(SemVer(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12"), SemVer.parse("1.2.3----RC-SNAPSHOT.12.9.1--.12"))
        assertEquals(
            SemVer(1, 0, 0, null, "0.build.1-rc.10000aaa-kk-0.1"),
            SemVer.parse("1.0.0+0.build.1-rc.10000aaa-kk-0.1")
        )
        assertEquals(SemVer(1, 0, 0, "0A.is.legal"), SemVer.parse("1.0.0-0A.is.legal"))
    }

    @Test
    fun parseExceedLongRange() {
        // The type for major/minor/patch are Long, thus throw exception
        assertFailsWith<NumberFormatException> { SemVer.parse("99999999999999999999999.999999999999999999.99999999999999999") }
    }

    /**
     * Cases from the [suggested regular expression](https://regex101.com/r/vkijKf/1/).
     */
    @Test
    fun parseInvalid() {
        assertFailsWith<IllegalArgumentException> { SemVer.parse("v1.2.3") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse(" 1.2.3") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2.3 ") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("a") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2.3-0123") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2.3-0123.0123") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.1.2+.123") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("+invalid") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("-invalid") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("-invalid+invalid") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("-invalid.01") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("alpha") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("alpha.beta") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("alpha.beta.1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("alpha.1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("alpha+beta") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("alpha_beta") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("alpha.") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("alpha..") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("beta") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.0.0-alpha_beta") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("-alpha.") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.0.0-alpha..") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.0.0-alpha..1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.0.0-alpha...1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.0.0-alpha....1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.0.0-alpha.....1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.0.0-alpha......1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.0.0-alpha.......1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("01.1.1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.01.1") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.1.01") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2.3.DEV") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2-SNAPSHOT") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2.31.2.3----RC-SNAPSHOT.12.09.1--..12+788") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("1.2-RC-SNAPSHOT") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("-1.0.3-gamma+b7718") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("+justmeta") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("9.8.7+meta+meta") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("9.8.7-whatever+meta+meta") }
        assertFailsWith<IllegalArgumentException> { SemVer.parse("99999999999999999999999.999999999999999999.99999999999999999----RC-SNAPSHOT.12.09.1--------------------------------..12") }
    }
}

