package net.swiftzer.semver

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
class SemVerSerializerTest : FunSpec({
    val json = Json
    test("descriptor") {
        SemVerSerializer.descriptor.apply {
            serialName shouldBe "SemVer"
            kind shouldBe PrimitiveKind.STRING
        }
    }

    test("serialize") {
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

    test("deserialize success") {
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

    test("deserialize IllegalArgumentException") {
        shouldThrow<SerializationException> {
            json.decodeFromString<DummyData>(
                """{"semVer":"1.1.2+.123","number":12345}""",
            )
        }.cause.shouldBeInstanceOf<IllegalArgumentException>()
    }

    test("deserialize NumberFormatException") {
        shouldThrow<SerializationException> {
            json.decodeFromString<DummyData>(
                """{"semVer":"99999999999999999999999.999999999999999999.99999999999999999","number":12345}""",
            )
        }.cause.shouldBeInstanceOf<NumberFormatException>()
    }
})

@Serializable
private data class DummyData(
    val semVer: SemVer,
    val number: Int,
)
