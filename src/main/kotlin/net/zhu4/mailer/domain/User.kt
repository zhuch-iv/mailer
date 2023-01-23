package net.zhu4.mailer.domain

import net.zhu4.mailer.domain.User.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(COLLECTION_NAME)
data class User(
    @Id
    val id: ObjectId,
    val discordId: Long,
    val allowed: Boolean,
    val authorization: EveAuthorization? = null,
    val character: Character? = null,
    val interactions: MutableMap<String, Interaction> = mutableMapOf()
) {

    fun clearInteraction(interactionId: String): User {
        this.interactions.clear()
        return this
    }

    fun createInteraction(): Interaction {
        val new = Interaction()
        this.interactions[new.getKey()] = new
        return new
    }

    fun appendMessage(key: String, messageId: Long, channelId: Long): User {
        this.interactions[key] = interactions[key]!!.copy(messageId = messageId, channelId = channelId)
        return this
    }

    companion object {
        const val COLLECTION_NAME = "users"
    }
}

data class Interaction(
    val id: ObjectId = ObjectId(),
    val messageId: Long? = null,
    val channelId: Long? = null
) {

    fun getKey(): String {
        return id.toHexString()
    }
}
