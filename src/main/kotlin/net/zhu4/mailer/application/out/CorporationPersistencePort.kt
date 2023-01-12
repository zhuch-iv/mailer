package net.zhu4.mailer.application.out

import reactor.core.publisher.Mono

interface CorporationPersistencePort {

    fun findAllowedCorporationIds(): Mono<List<Int>>
}
