package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.application.out.CharacterPersistencePort
import net.zhu4.mailer.domain.Character
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Level

@Component
class CharacterMongoAdapter(
    private val repository: CharacterMongoRepository
) : CharacterPersistencePort {

    override fun insert(characters: List<Character>): Mono<List<Character>> {
        return repository.insert(characters)
            .collectList()
            .log(CharacterMongoAdapter::class.qualifiedName, Level.FINEST)
    }

    override fun findByIds(ids: List<Int>): Mono<List<Character>> {
        return repository.findByIdIn(ids)
            .collectList()
            .log(CharacterMongoAdapter::class.qualifiedName, Level.FINEST)
    }
}
