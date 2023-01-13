package net.zhu4.mailer.adapter.eve.oauth

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import org.jose4j.jwk.JsonWebKey
import org.jose4j.lang.JoseException
import java.io.IOException


class JWKJsonDeserializer : StdDeserializer<JsonWebKey>(JsonWebKey::class.java) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): JsonWebKey {
        return try {
            val params: Map<String, Any> = jsonParser.readValueAs(object : TypeReference<Map<String, Any>>() {})
            JsonWebKey.Factory.newJwk(params)
        } catch (e: JoseException) {
            throw JsonParseException(jsonParser, "Unable to parse Json Web Key")
        }
    }
}
