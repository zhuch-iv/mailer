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

        fun List<Character>.fromCharactersAppendCeoName(ceoId: Int, ceoName: String): List<MailList> {
            return this
                .chunked(MAIL_LIST_MAX_CAPACITY - 1)
                .map {
                    MailList(
                        it.joinToString(separator = ",\n") {
                            ch -> "<a href=showinfo:1385//${ch.id}>${ch.name}</a>"
                        }
                            + ",\n<a href=showinfo:1385//${ceoId}>${ceoName}</a>"
                    )
                }
        }

        private const val MAIL_LIST_MAX_CAPACITY: Int = 50
    }
}
