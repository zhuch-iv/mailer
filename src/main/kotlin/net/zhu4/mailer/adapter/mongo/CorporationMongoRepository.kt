package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.domain.Corporation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface CorporationMongoRepository : ReactiveMongoRepository<Corporation, Int> {

    fun findByAllowedIsTrue(): Flux<Corporation>
}
