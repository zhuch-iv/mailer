package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.domain.ApplicationCommand
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux

interface ApplicationCommandMongoRepository : ReactiveMongoRepository<ApplicationCommand, ObjectId> {

    fun findByRegisteredIsFalse(): Flux<ApplicationCommand>
}
