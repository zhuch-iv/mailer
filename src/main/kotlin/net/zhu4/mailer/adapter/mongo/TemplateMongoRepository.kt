package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.domain.Template
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface TemplateMongoRepository : ReactiveMongoRepository<Template, ObjectId> {

    fun findByName(name: String): Mono<Template>
}
