package net.zhu4.mailer.domain

import net.zhu4.mailer.domain.User.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(COLLECTION_NAME)
class User(
    @Id
    val id: ObjectId,
    val discordId: Long,
    val allowed: Boolean
) {
    companion object {
        const val COLLECTION_NAME = "users"
    }
}