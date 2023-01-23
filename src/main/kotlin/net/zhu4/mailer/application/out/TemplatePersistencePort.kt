package net.zhu4.mailer.application.out

import net.zhu4.mailer.domain.Template
import org.bson.types.ObjectId
import reactor.core.publisher.Mono

interface TemplatePersistencePort {

    fun findById(id: ObjectId): Mono<Template>

    fun findByName(name: String): Mono<Template>

    fun save(template: Template): Mono<Template>
}
