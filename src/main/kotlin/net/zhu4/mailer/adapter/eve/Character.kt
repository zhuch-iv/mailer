package net.zhu4.mailer.adapter.eve

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Character(
    val allianceId: Int?,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    val birthday: LocalDateTime,
    val bloodlineId: Int,
    val corporationId: Int,
    val description: String?,
    val factionId: Int?,
    val gender: Gender,
    val name: String,
    val raceId: Int,
    val securityStatus: Float?,
    val title: String?
) {

    fun toDomain(characterId: Int): net.zhu4.mailer.domain.Character {
        return net.zhu4.mailer.domain.Character(
            characterId, birthday, corporationId, name, securityStatus
        )
    }
}

enum class Gender {
    @JsonProperty("female")
    FEMALE,
    @JsonProperty("male")
    MALE;
}
