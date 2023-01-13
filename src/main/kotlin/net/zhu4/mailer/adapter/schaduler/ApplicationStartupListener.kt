package net.zhu4.mailer.adapter.schaduler

import net.zhu4.mailer.application.`in`.UploadApplicationCommandsUseCase
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class ApplicationStartupListener(
    private val useCase: UploadApplicationCommandsUseCase
) : ApplicationListener<ApplicationReadyEvent> {

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        useCase.uploadApplicationCommands()
            .subscribe { list -> list.forEach { log.info("Uploaded command: $it") } }
    }

    companion object {
        private val log = LoggerFactory.getLogger(ApplicationStartupListener::class.java)
    }
}
