package dev.aurakai.auraframefx.api.client.infrastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object StringBuilderAdapter : KSerializer<StringBuilder> {
    override fun serialize(encoder: Encoder, value: StringBuilder) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): StringBuilder =
        StringBuilder(decoder.decodeString())

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StringBuilder", PrimitiveKind.STRING)
}
