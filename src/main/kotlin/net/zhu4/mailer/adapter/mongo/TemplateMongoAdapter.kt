package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.application.out.TemplatePersistencePort
import net.zhu4.mailer.domain.Template
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.logging.Level

@Component
class TemplateMongoAdapter(
    private val repository: TemplateMongoRepository
) : TemplatePersistencePort {

    override fun findById(id: ObjectId): Mono<Template> {
        return repository.findById(id)
            .log(TemplateMongoAdapter::class.qualifiedName, Level.FINEST)
    }

    override fun findByName(name: String): Mono<Template> {
        return repository.findByName(name)
            .log(TemplateMongoAdapter::class.qualifiedName, Level.FINEST)
    }

    override fun save(template: Template): Mono<Template> {
        return repository.save(template)
            .log(TemplateMongoAdapter::class.qualifiedName, Level.FINEST)
    }
}
