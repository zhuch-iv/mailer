package net.zhu4.mailer.adapter.eve.oauth

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.jose4j.jwk.JsonWebKey
import org.jose4j.keys.resolvers.JwksVerificationKeyResolver

@JsonIgnoreProperties(ignoreUnknown = true)
class JsonWebKeys @JsonCreator constructor() {
    @JsonSerialize(contentUsing = JWKJsonSerializer::class)
    @JsonDeserialize(contentUsing = JWKJsonDeserializer::class)
    lateinit var keys: List<JsonWebKey>

    fun keyResolver(): JwksVerificationKeyResolver {
        return JwksVerificationKeyResolver(keys)
    }
}
