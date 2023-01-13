package net.zhu4.mailer.adapter.mongo

import net.zhu4.mailer.application.out.ApplicationCommandPersistencePort
import net.zhu4.mailer.domain.ApplicationCommand
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import java.util.logging.Level

@Component
class ApplicationCommandMongoAdapter(
    private val repository: ApplicationCommandMongoRepository
) : ApplicationCommandPersistencePort {

    override fun save(commands: Flux<ApplicationCommand>): Flux<ApplicationCommand> {
        return repository.saveAll(commands)
            .log(ApplicationCommandMongoAdapter::class.qualifiedName, Level.FINEST)
    }

    override fun findNotRegistered(): Flux<ApplicationCommand> {
        return repository.findByRegisteredIsFalse()
            .log(ApplicationCommandMongoAdapter::class.qualifiedName, Level.FINEST)
    }
}
