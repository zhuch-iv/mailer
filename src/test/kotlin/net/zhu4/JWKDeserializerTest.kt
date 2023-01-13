package net.zhu4

import com.fasterxml.jackson.databind.ObjectMapper
import net.zhu4.mailer.adapter.eve.oauth.JsonWebKeys
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class JWKDeserializerTest {

    private val objectMapper = ObjectMapper()

    @Test
    fun `deserialization test`() {
        val keys = objectMapper.readValue(testKeysJson, JsonWebKeys::class.java).keys
        Assertions.assertEquals("RS256", keys[0].algorithm)
        Assertions.assertEquals("ES256", keys[1].algorithm)
    }

    companion object {
        private const val testKeysJson = "{\"keys\":[{\"alg\":\"RS256\",\"e\":\"AQAB\",\"kid\":\"JWT-Signature-Key\",\"kty\":\"RSA\",\"n\":\"nehPQ7FQ1YK-leKyIg-aACZaT-DbTL5V1XpXghtLX_bEC-fwxhdE_4yQKDF6cA-V4c-5kh8wMZbfYw5xxgM9DynhMkVrmQFyYB3QMZwydr922UWs3kLz-nO6vi0ldCn-ffM9odUPRHv9UbhM5bB4SZtCrpr9hWQgJ3FjzWO2KosGQ8acLxLtDQfU_lq0OGzoj_oWwUKaN_OVfu80zGTH7mxVeGMJqWXABKd52ByvYZn3wL_hG60DfDWGV_xfLlHMt_WoKZmrXT4V3BCBmbitJ6lda3oNdNeHUh486iqaL43bMR2K4TzrspGMRUYXcudUQ9TycBQBrUlT85NRY9TeOw\",\"use\":\"sig\"},{\"alg\":\"ES256\",\"crv\":\"P-256\",\"kid\":\"8878a23f-2489-4045-989e-4d2f3ec1ae1a\",\"kty\":\"EC\",\"use\":\"sig\",\"x\":\"PatzB2HJzZOzmqQyYpQYqn3SAXoVYWrZKmMgJnfK94I\",\"y\":\"qDb1kUd13fRTN2UNmcgSoQoyqeF_C1MsFlY_a87csnY\"}],\"SkipUnresolvedJsonWebKeys\":true}"
    }
}
