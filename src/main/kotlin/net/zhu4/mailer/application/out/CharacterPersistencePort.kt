package net.zhu4.mailer.application.out

import net.zhu4.mailer.domain.Character
import reactor.core.publisher.Mono

interface CharacterPersistencePort {

    fun insert(characters: List<Character>): Mono<List<Character>>

    fun findByIds(ids: List<Int>): Mono<List<Character>>
}
