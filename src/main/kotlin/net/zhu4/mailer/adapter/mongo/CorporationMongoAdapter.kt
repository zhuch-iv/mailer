package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.application.out.CorporationPersistencePort
import net.zhu4.mailer.domain.Corporation
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Level

@Component
class CorporationMongoAdapter(
    private val repository: CorporationMongoRepository
): CorporationPersistencePort {

    override fun findAllowedCorporationIds(): Mono<List<Int>> {
        return repository.findByAllowedIsTrue()
            .map(Corporation::id)
            .collectList()
            .log(CorporationMongoAdapter::class.qualifiedName, Level.FINEST)
    }
}
