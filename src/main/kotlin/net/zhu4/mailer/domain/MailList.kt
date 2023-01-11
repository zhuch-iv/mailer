package net.zhu4.mailer.domain

data class MailList(
    val characterNames: String
) {

    companion object {

        fun List<Character>.fromCharacters(): List<MailList> {
            return this.map(Character::name)
                .chunked(MAIL_LIST_MAX_CAPACITY)
                .map { MailList(it.joinToString(separator = ", ")) }
        }

        fun List<Character>.fromCharactersAppendCeoName(ceoName: String): List<MailList> {
            return this.map(Character::name)
                .chunked(MAIL_LIST_MAX_CAPACITY - 1)
                .map { MailList((it + ceoName).joinToString(separator = ", ")) }
        }

        private const val MAIL_LIST_MAX_CAPACITY: Int = 50
    }
}
