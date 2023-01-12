package net.zhu4.mailer.adapter.eve

data class Characters(
    val characters: List<CharacterToId>?
)

data class CharacterToId(
    val id: Int,
    val name: String
)
