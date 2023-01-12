package net.zhu4.mailer.application.`in`

import net.zhu4.mailer.application.out.CharacterPersistencePort
import net.zhu4.mailer.application.out.CorporationPersistencePort
import net.zhu4.mailer.domain.Character
import net.zhu4.mailer.domain.MailList
import net.zhu4.mailer.domain.MailList.Companion.fromCharactersAppendCeoName
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FormMailListsUseCaseImpl(
    private val corporationPort: CorporationPersistencePort,
    private val characterPort: CharacterPersistencePort
) : FormMailListsUseCase {

    @Value("\${mailer.ceoName}")
    private lateinit var ceoName: String

    @Value("\${mailer.ceoId}")
    private lateinit var ceoId: String

    override fun formMailLists(characters: Mono<List<Character>>): Mono<List<MailList>> {
        return characters
            .filterAllowedCorporationCharacters()
            .saveCharacters()
            .map { it.fromCharactersAppendCeoName(ceoId.toInt(), ceoName) }
    }

    private fun Mono<List<Character>>.filterAllowedCorporationCharacters(): Mono<List<Character>> {
        return this.flatMap {
            corporationPort.findAllowedCorporationIds()
                .map { corps ->
                    it.filter { ch -> corps.contains(ch.corporationId) }
                }
        }
    }

    private fun Mono<List<Character>>.saveCharacters(): Mono<List<Character>> {
        return this.flatMap { characterPort.insert(it.map { ch -> ch.copy(isMailed = true) }) }
    }
}
