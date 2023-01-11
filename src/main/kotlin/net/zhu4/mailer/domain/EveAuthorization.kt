package net.zhu4.mailer.domain

import net.zhu4.mailer.domain.EveAuthorization.Companion.COLLECTION_NAME
import org.springframework.data.mongodb.core.mapping.Document

@Document(COLLECTION_NAME)
data class EveAuthorization(
    val characterId: Int,
    val token: String
) {
    companion object {
        const val COLLECTION_NAME = "auth"
    }
}
