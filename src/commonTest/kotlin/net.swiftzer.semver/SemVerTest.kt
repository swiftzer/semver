package net.swiftzer.semver

import kotlin.test.*


class SemVerTest {
    @Test
    fun initValid() {
        SemVer(12, 23, 34, "alpha.12", "test.34")
    }

    @Test
    fun initInvalidMajor() {
        assertFails { SemVer(-1, 23, 34, "alpha.12", "test.34") }
    }

    @Test
    fun initInvalidMinor() {
        assertFails { SemVer(12, -1, 34, "alpha.12", "test.34") }
    }

    @Test
    fun initInvalidPatch() {
        assertFails { SemVer(12, 23, -1, "alpha.12", "test.34") }
    }

    @Test
    fun initInvalidPreRelease() {
        assertFails { SemVer(12, 23, 34, "alpha.12#", "test.34") }
    }

    @Test
    fun initInvalidMetadata() {
        assertFails { SemVer(12, 23, 34, "alpha.12", "test.34#") }
    }

    @Test
    fun parseNumeric() {
        val actual = SemVer.parse("1.0.45")
        val expected = SemVer(1, 0, 45)
        assertEquals(expected, actual)
    }

    @Test
    fun parseIncompleteNumeric1() {
        val actual = SemVer.parse("432")
        val expected = SemVer(432)
        assertEquals(expected, actual)
    }

    @Test
    fun parseIncompleteNumeric2() {
        val actual = SemVer.parse("53.203")
        val expected = SemVer(53, 203)
        assertEquals(expected, actual)
    }

    @Test
    fun parseIncompleteNumeric3() {
        val actual = SemVer.parse("2..235")
        val expected = SemVer(2, 0, 235)
        assertEquals(expected, actual)
    }

    @Test
    fun parsePreRelease() {
        val actual = SemVer.parse("1.0.0-alpha.beta-a.12")
        val expected = SemVer(1, 0, 0, preRelease = "alpha.beta-a.12")
        assertEquals(expected, actual)
    }

    @Test
    fun parseIncompletePreRelease() {
        val actual = SemVer.parse("34..430-alpha.beta.gamma-a.12")
        val expected = SemVer(34, 0, 430, preRelease = "alpha.beta.gamma-a.12")
        assertEquals(expected, actual)
    }

    @Test
    fun parseMetadata() {
        val actual = SemVer.parse("1.0.0+exp.sha-part.5114f85")
        val expected = SemVer(1, 0, 0, buildMetadata = "exp.sha-part.5114f85")
        assertEquals(expected, actual)
    }

    @Test
    fun parseIncompleteMetadata() {
        val actual = SemVer.parse("88.30+exp.sha-part.5114f85")
        val expected = SemVer(88, 30, 0, buildMetadata = "exp.sha-part.5114f85")
        assertEquals(expected, actual)
    }

    @Test
    fun parseAll() {
        val actual = SemVer.parse("1.0.0-beta+exp.sha.5114f85")
        val expected = SemVer(1, 0, 0, preRelease = "beta", buildMetadata = "exp.sha.5114f85")
        assertEquals(expected, actual)
    }

    @Test
    fun parseIncompleteAll1() {
        val actual = SemVer.parse("..-beta+exp.sha.5114f85")
        val expected = SemVer(0, 0, 0, preRelease = "beta", buildMetadata = "exp.sha.5114f85")
        assertEquals(expected, actual)
    }

    @Test
    fun parseIncompleteAll2() {
        val actual = SemVer.parse(".34.-beta+exp.sha.5114f85")
        val expected = SemVer(0, 34, 0, preRelease = "beta", buildMetadata = "exp.sha.5114f85")
        assertEquals(expected, actual)
    }

    @Test
    fun parseInvalid() {
        assertFails { SemVer.parse("1.0.1.4-beta+exp.sha.5114f85") }
    }

    @Test
    fun isInitialDevelopmentPhaseTrue() {
        assertTrue { SemVer(0, 23, 34, "alpha.123", "testing.123").isInitialDevelopmentPhase() }
    }

    @Test
    fun isInitialDevelopmentPhaseFalse() {
        assertFalse { SemVer(1, 23, 34, "alpha.123", "testing.123").isInitialDevelopmentPhase() }
    }

    @Test
    fun toStringNumeric() {
        val semVer = SemVer(1, 0, 45)
        assertEquals("1.0.45", semVer.toString())
    }

    @Test
    fun toStringPreRelease() {
        val semVer = SemVer(1, 0, 0, preRelease = "alpha.beta-a.12")
        assertEquals("1.0.0-alpha.beta-a.12", semVer.toString())
    }

    @Test
    fun toStringMetadata() {
        val semVer = SemVer(1, 0, 0, buildMetadata = "exp.sha-part.5114f85")
        assertEquals("1.0.0+exp.sha-part.5114f85", semVer.toString())
    }

    @Test
    fun toStringAll() {
        val semVer = SemVer(1, 0, 0, preRelease = "beta", buildMetadata = "exp.sha.5114f85")
        assertEquals("1.0.0-beta+exp.sha.5114f85", semVer.toString())
    }

    @Test
    fun compareToNumeric1() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 0, 0)
        assertEquals(0, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToNumeric2() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(2, 0, 0)
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToNumeric3() {
        val semVer1 = SemVer(2, 0, 0)
        val semVer2 = SemVer(2, 1, 0)
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToNumeric4() {
        val semVer1 = SemVer(2, 1, 4)
        val semVer2 = SemVer(2, 1, 0)
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToNumeric5() {
        val semVer1 = SemVer(2, 0, 0)
        val semVer2 = SemVer(1, 0, 0)
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToNumeric6() {
        val semVer1 = SemVer(1, 2, 0)
        val semVer2 = SemVer(1, 0, 0)
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToNumeric7() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 0, 2)
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease1() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease2() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha")
        val semVer2 = SemVer(1, 0, 0)
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease3() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease4() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease5() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.beta")
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease6() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.beta")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease7() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "beta")
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease8() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "beta")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease9() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.2")
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease10() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.2")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease11() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(0, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreRelease12() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha")
        assertEquals(0, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreReleaseMetadata() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha", buildMetadata = "xyz")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha", buildMetadata = "abc")
        assertEquals(0, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareToPreReleaseNonNumericSmaller() {
        val semVer1 = SemVer(1, 0, 0, preRelease = Int.MAX_VALUE.toString())
        val semVer2 = SemVer(1, 0, 0, preRelease = Long.MAX_VALUE.toString())
        assertTrue { semVer1 < semVer2 }
    }

    @Test
    fun compareToPreReleaseNonNumericLarger() {
        val semVer1 = SemVer(1, 0, 0, preRelease = Long.MAX_VALUE.toString())
        val semVer2 = SemVer(1, 0, 0, preRelease = Int.MAX_VALUE.toString())
        assertTrue { semVer1 > semVer2 }
    }

    @Test
    fun compareToPreReleaseNonNumericSame() {
        val semVer1 = SemVer(1, 0, 0, preRelease = Long.MAX_VALUE.toString())
        val semVer2 = SemVer(1, 0, 0, preRelease = Long.MAX_VALUE.toString())
        assertEquals(0, semVer1.compareTo(semVer2))
    }

    @Test
    fun createsNextMajor() {
        val newMajor = SemVer(1, 3, 5).nextMajor()
        assertEquals(2, newMajor.major)
        assertEquals(0, newMajor.minor)
        assertEquals(0, newMajor.patch)
    }

    @Test
    fun createsNextMinor() {
        val newMinor = SemVer(1, 3, 5).nextMinor()
        assertEquals(1, newMinor.major)
        assertEquals(4, newMinor.minor)
        assertEquals(0, newMinor.patch)
    }

    @Test
    fun createsNextPatch() {
        val newPatch = SemVer(1, 3, 5).nextPatch()
        assertEquals(1, newPatch.major)
        assertEquals(3, newPatch.minor)
        assertEquals(6, newPatch.patch)
    }
}