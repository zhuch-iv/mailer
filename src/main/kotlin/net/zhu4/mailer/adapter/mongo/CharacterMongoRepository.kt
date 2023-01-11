package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.domain.Character
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface CharacterMongoRepository : ReactiveMongoRepository<Character, Int> {

    fun findByIdIn(ids: List<Int>) : Flux<Character>
}
