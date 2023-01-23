package net.zhu4.mailer.domain

import net.zhu4.mailer.domain.Character.Companion.COLLECTION_NAME
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(COLLECTION_NAME)
data class Character(
    @Id
    val id: Int,
    val birthday: LocalDateTime,
    val corporationId: Int,
    val name: String,
    val securityStatus: Float?,
    val isMailed: Boolean = false
) {

    fun toMailRecipient(): Recipient {
        return Recipient(
            recipientId = id,
            recipientType = RecipientType.CHARACTER
        )
    }

    companion object {
        const val COLLECTION_NAME = "characters"
    }
}

data class EveAuthorization(
    val accessToken: String,
    val refreshToken: String,
)
