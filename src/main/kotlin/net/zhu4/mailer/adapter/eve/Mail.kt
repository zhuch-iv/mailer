package net.zhu4.mailer.adapter.eve

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import net.zhu4.mailer.adapter.eve.RecipientType.Companion.fromDomain
import java.time.LocalDateTime

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Mail(
    val approvedCost: Long,
    val body: String,
    val recipients: List<Recipient>,
    val subject: String
) {
    companion object {
        fun net.zhu4.mailer.domain.Mail.fromDomain(): Mail {
            return Mail(
                approvedCost, body, recipients.map(Recipient::fromDomain), subject
            )
        }
    }
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class MailHeader(
    val from: Int,
    val isRead: Boolean,
    val labels: List<Int>,
    val mailId: Int,
    val recipients: List<Recipient>,
    val subject: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val timestamp: LocalDateTime
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Recipient(
    val recipientId: Int,
    val recipientType: RecipientType
) {
    companion object {
        fun fromDomain(recipient: net.zhu4.mailer.domain.Recipient): Recipient {
            return Recipient(recipient.recipientId, recipient.recipientType.fromDomain())
        }
    }
}

enum class RecipientType {
    @JsonProperty("alliance")
    ALLIANCE,
    @JsonProperty("character")
    CHARACTER,
    @JsonProperty("corporation")
    CORPORATION,
    @JsonProperty("mailing_list")
    MAILING_LIST;

    companion object {
        fun net.zhu4.mailer.domain.RecipientType.fromDomain(): RecipientType {
            return RecipientType.valueOf(this.name)
        }
    }
}
