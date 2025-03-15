package net.swiftzer.semver

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.json.Json
import kotlin.test.Test

class SemVerSerializerTest {
    private val json = Json

    @Test
    fun descriptor() {
        SemVerSerializer.descriptor.apply {
            serialName shouldBe "SemVer"
            kind shouldBe PrimitiveKind.STRING
        }
    }

    @Test
    fun serialize() {
        json.encodeToString(
            DummyData(
                semVer = SemVer(
                    major = 2,
                    minor = 0,
                    patch = 0,
                    preRelease = "rc.1",
                    buildMetadata = "build.123",
                ),
                number = 12345,
            ),
        ) shouldBe """{"semVer":"2.0.0-rc.1+build.123","number":12345}"""
    }

    @Test
    fun deserializeSuccess() {
        json.decodeFromString<DummyData>(
            """{"semVer":"2.0.0-rc.1+build.123","number":12345}""",
        ) shouldBe DummyData(
            semVer = SemVer(
                major = 2,
                minor = 0,
                patch = 0,
                preRelease = "rc.1",
                buildMetadata = "build.123",
            ),
            number = 12345,
        )
    }

    @Test
    fun deserializeIllegalArgumentException() {
        shouldThrow<SerializationException> {
            json.decodeFromString<DummyData>(
                """{"semVer":"1.1.2+.123","number":12345}""",
            )
        }.cause.shouldBeInstanceOf<IllegalArgumentException>()
    }

    @Test
    fun deserializeNumberFormatException() {
        shouldThrow<SerializationException> {
            json.decodeFromString<DummyData>(
                """{"semVer":"99999999999999999999999.999999999999999999.99999999999999999","number":12345}""",
            )
        }.cause.shouldBeInstanceOf<NumberFormatException>()
    }
}

@Serializable
private data class DummyData(
    val semVer: SemVer,
    val number: Int,
)
