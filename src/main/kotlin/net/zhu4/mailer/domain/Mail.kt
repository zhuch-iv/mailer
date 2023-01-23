package net.zhu4.mailer.domain

class Mail(
    val approvedCost: Long = 1_000_000,
    val body: String,
    val recipients: List<Recipient>,
    val subject: String
)

data class Recipient(
    val recipientId: Int,
    val recipientType: RecipientType
)

enum class RecipientType {
    ALLIANCE,
    CHARACTER,
    CORPORATION,
    MAILING_LIST;
}
