package net.swiftzer.semver

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class SemVerTest {
    @Test
    fun init_valid() {
        SemVer(12, 23, 34, "alpha.12", "test.34")
    }

    @Test
    fun init_invalid_major() {
        assertFails { SemVer(-1, 23, 34, "alpha.12", "test.34") }
    }

    @Test
    fun init_invalid_minor() {
        assertFails { SemVer(12, -1, 34, "alpha.12", "test.34") }
    }

    @Test
    fun init_invalid_patch() {
        assertFails { SemVer(12, 23, -1, "alpha.12", "test.34") }
    }

    @Test
    fun init_invalid_preRelease() {
        assertFails { SemVer(12, 23, 34, "alpha.12#", "test.34") }
    }

    @Test
    fun init_invalid_metadata() {
        assertFails { SemVer(12, 23, 34, "alpha.12", "test.34#") }
    }

    @Test
    fun parse_numeric() {
        val actual = SemVer.parse("1.0.45")
        val expected = SemVer(1, 0, 45)
        assertEquals(expected, actual)
    }

    @Test
    fun parse_preRelease() {
        val actual = SemVer.parse("1.0.0-alpha.beta-a.12")
        val expected = SemVer(1, 0, 0, preRelease = "alpha.beta-a.12")
        assertEquals(expected, actual)
    }

    @Test
    fun parse_metadata() {
        val actual = SemVer.parse("1.0.0+exp.sha-part.5114f85")
        val expected = SemVer(1, 0, 0, buildMetadata = "exp.sha-part.5114f85")
        assertEquals(expected, actual)
    }

    @Test
    fun parse_all() {
        val actual = SemVer.parse("1.0.0-beta+exp.sha.5114f85")
        val expected = SemVer(1, 0, 0, preRelease = "beta", buildMetadata = "exp.sha.5114f85")
        assertEquals(expected, actual)
    }

    @Test
    fun parse_invalid() {
        assertFails { SemVer.parse("1.0.1.4-beta+exp.sha.5114f85") }
    }

    @Test
    fun isInitialDevelopmentPhase_true() {
        assertTrue { SemVer(0, 23, 34, "alpha.123", "testing.123").isInitialDevelopmentPhase() }
    }

    @Test
    fun isInitialDevelopmentPhase_false() {
        assertFalse { SemVer(1, 23, 34, "alpha.123", "testing.123").isInitialDevelopmentPhase() }
    }

    @Test
    fun toString_numeric() {
        val semVer = SemVer(1, 0, 45)
        assertEquals("1.0.45", semVer.toString())
    }

    @Test
    fun toString_preRelease() {
        val semVer = SemVer(1, 0, 0, preRelease = "alpha.beta-a.12")
        assertEquals("1.0.0-alpha.beta-a.12", semVer.toString())
    }

    @Test
    fun toString_metadata() {
        val semVer = SemVer(1, 0, 0, buildMetadata = "exp.sha-part.5114f85")
        assertEquals("1.0.0+exp.sha-part.5114f85", semVer.toString())
    }

    @Test
    fun toString_all() {
        val semVer = SemVer(1, 0, 0, preRelease = "beta", buildMetadata = "exp.sha.5114f85")
        assertEquals("1.0.0-beta+exp.sha.5114f85", semVer.toString())
    }

    @Test
    fun compareTo_numeric1() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 0, 0)
        assertEquals(0, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_numeric2() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(2, 0, 0)
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_numeric3() {
        val semVer1 = SemVer(2, 0, 0)
        val semVer2 = SemVer(2, 1, 0)
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_numeric4() {
        val semVer1 = SemVer(2, 1, 4)
        val semVer2 = SemVer(2, 1, 0)
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_numeric5() {
        val semVer1 = SemVer(2, 0, 0)
        val semVer2 = SemVer(1, 0, 0)
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_numeric6() {
        val semVer1 = SemVer(1, 2, 0)
        val semVer2 = SemVer(1, 0, 0)
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_numeric7() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 0, 2)
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease1() {
        val semVer1 = SemVer(1, 0, 0)
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease2() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha")
        val semVer2 = SemVer(1, 0, 0)
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease3() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease4() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease5() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.beta")
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease6() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.beta")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease7() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "beta")
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease8() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "beta")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease9() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.2")
        assertEquals(-1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease10() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.2")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(1, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease11() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha.1")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha.1")
        assertEquals(0, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease12() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha")
        assertEquals(0, semVer1.compareTo(semVer2))
    }

    @Test
    fun compareTo_preRelease_metadata() {
        val semVer1 = SemVer(1, 0, 0, preRelease = "alpha", buildMetadata = "xyz")
        val semVer2 = SemVer(1, 0, 0, preRelease = "alpha", buildMetadata = "abc")
        assertEquals(0, semVer1.compareTo(semVer2))
    }
}