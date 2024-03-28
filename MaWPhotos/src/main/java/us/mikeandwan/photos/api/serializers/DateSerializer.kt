package us.mikeandwan.photos.api.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {
    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)

    override fun deserialize(decoder: Decoder): Date {
        val dateString = decoder.decodeString()
        return format.parse(dateString)
    }

    override fun serialize(encoder: Encoder, value: Date) {
        val dateString = format.format(value)
        encoder.encodeString(dateString)
    }
}