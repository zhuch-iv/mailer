package net.zhu4.mailer.domain

import net.zhu4.mailer.domain.ApplicationCommand.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

// https://discord.com/developers/docs/interactions/application-commands

@Document(COLLECTION_NAME)
data class ApplicationCommand(
    @Id
    val id: ObjectId = ObjectId(),
    val name: String,
    val description: String,
    val type: Int = 1,
    val options: List<ApplicationCommandOption> = listOf(),
    val guildId: Long? = null,
    val registered: Boolean = false
) {

    companion object {
        const val COLLECTION_NAME = "application_commands"
    }
}

data class ApplicationCommandOption(
    val name: String,
    val description: String,
    val type: Int,
    val options: List<ApplicationCommandOption> = listOf(),
    val required: Boolean = false
)
