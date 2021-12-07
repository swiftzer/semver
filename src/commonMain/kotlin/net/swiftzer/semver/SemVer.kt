package net.swiftzer.semver

import kotlin.jvm.JvmStatic
import kotlin.math.min

/**
 * Version number in [Semantic Versioning 2.0.0](http://semver.org/spec/v2.0.0.html) specification (SemVer).
 *
 * @property major major version, increment it when you make incompatible API changes.
 * @property minor minor version, increment it when you add functionality in a backwards-compatible manner.
 * @property patch patch version, increment it when you make backwards-compatible bug fixes.
 * @property preRelease pre-release version.
 * @property buildMetadata build metadata.
 */
data class SemVer(
    val major: Int = 0,
    val minor: Int = 0,
    val patch: Int = 0,
    val preRelease: String? = null,
    val buildMetadata: String? = null,
) : Comparable<SemVer> {

    init {
        require(major >= 0) { "Major version must be a positive number" }
        require(minor >= 0) { "Minor version must be a positive number" }
        require(patch >= 0) { "Patch version must be a positive number" }
        if (preRelease != null) require(preReleasePattern matches preRelease) { "Pre-release version is not valid" }
        if (buildMetadata != null) require(buildMetadataPattern matches buildMetadata) { "Build metadata is not valid" }
    }

    override fun compareTo(other: SemVer): Int {
        if (major > other.major) return 1
        if (major < other.major) return -1
        if (minor > other.minor) return 1
        if (minor < other.minor) return -1
        if (patch > other.patch) return 1
        if (patch < other.patch) return -1

        // When major, minor, and patch are equal, a pre-release version has lower precedence than a normal version
        if (preRelease != null && other.preRelease == null) return -1
        if (preRelease == null && other.preRelease != null) return 1
        if (preRelease == null && other.preRelease == null) return 0

        // Precedence for two pre-release versions with the same major, minor, and patch version MUST be determined by
        // comparing each dot separated identifier from left to right until a difference is found
        val parts = preRelease!!.split(".")
        val otherParts = other.preRelease!!.split(".")

        val smallerSize = min(parts.size, otherParts.size)
        for (i in 0 until smallerSize) {
            val part = parts[i]
            val otherPart = otherParts[i]
            if (part == otherPart) continue
            val partIsNumeric = part.isNumeric()
            val otherPartIsNumeric = otherPart.isNumeric()

            return when {
                partIsNumeric && !otherPartIsNumeric -> -1
                !partIsNumeric && otherPartIsNumeric -> 1
                !partIsNumeric && !otherPartIsNumeric -> part.compareTo(otherPart)
                else -> try {
                    val partLong = part.toLong()
                    val otherPartLong = otherPart.toLong()
                    partLong.compareTo(otherPartLong)
                } catch (_: NumberFormatException) {
                    // When part or otherPart doesn't fit in an Int, compare as String
                    part.compareTo(otherPart)
                }
            }
        }
        return when {
            parts.size == smallerSize && otherParts.size > smallerSize -> {
                // parts is ended and otherParts is not ended
                -1
            }
            parts.size > smallerSize && otherParts.size == smallerSize -> {
                // parts is not ended and otherParts is ended
                1
            }
            else -> 0
        }
    }

    /**
     * Check the version number is in initial development.
     *
     * @return true if it is in initial development.
     */
    fun isInitialDevelopmentPhase(): Boolean = major == 0

    /**
     * Build the version name string.
     *
     * @return version name string in Semantic Versioning 2.0.0 specification.
     */
    override fun toString(): String = buildString {
        append(major)
        append('.')
        append(minor)
        append('.')
        append(patch)
        if (preRelease != null) {
            append('-')
            append(preRelease)
        }
        if (buildMetadata != null) {
            append('+')
            append(buildMetadata)
        }
    }

    private fun String.isNumeric(): Boolean = numericPattern.matches(this)

    companion object {
        private val preReleasePattern =
            Regex("""(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*""")
        private val buildMetadataPattern = Regex("""[0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*""")
        private val fullPattern =
            Regex("""(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?""")
        private val numericPattern = Regex("""\d+""")

        /**
         * Parse the version string to [SemVer].
         *
         * @param version version string to be parsed.
         * @throws IllegalArgumentException if the given string is not a valid version.
         */
        @JvmStatic
        fun parse(version: String): SemVer {
            val (major, minor, patch, preRelease, buildMetadata) = (fullPattern.matchEntire(version)
                ?: throw IllegalArgumentException("Invalid version string [$version]")).destructured
            return SemVer(
                major = major.toInt(),
                minor = minor.toInt(),
                patch = patch.toInt(),
                preRelease = preRelease.ifEmpty { null },
                buildMetadata = buildMetadata.ifEmpty { null },
            )
        }
    }
}
