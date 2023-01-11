package net.zhu4.mailer.domain

import net.zhu4.mailer.domain.Corporation.Companion.COLLECTION_NAME
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(COLLECTION_NAME)
data class Corporation(
    @Id
    val id: Int,
    val name: String,
    val allowed: Boolean
) {
    companion object {
        const val COLLECTION_NAME = "corporations"
    }
}
