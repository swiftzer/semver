package net.swiftzer.semver

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object SemVerSerializer : KSerializer<SemVer> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "SemVer",
        kind = PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: SemVer) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): SemVer = try {
        SemVer.parse(decoder.decodeString())
    } catch (e: IllegalArgumentException) {
        throw SerializationException(e)
    }
}
