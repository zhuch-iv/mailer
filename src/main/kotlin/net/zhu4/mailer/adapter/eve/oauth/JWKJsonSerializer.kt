package net.zhu4.mailer.adapter.eve.oauth

import com.fasterxml.jackson.core.JsonGenerationException
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.jose4j.jwk.JsonWebKey
import org.jose4j.jwk.JsonWebKey.OutputControlLevel
import java.io.IOException


class JWKJsonSerializer : StdSerializer<JsonWebKey>(JsonWebKey::class.java) {
    @Throws(IOException::class, JsonGenerationException::class)
    override fun serialize(jsonWebKey: JsonWebKey, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeObject(jsonWebKey.toParams(OutputControlLevel.INCLUDE_SYMMETRIC))
    }
}
