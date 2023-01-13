package net.zhu4.mailer.application.`in`

import discord4j.core.GatewayDiscordClient
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import net.zhu4.mailer.application.out.ApplicationCommandPersistencePort
import net.zhu4.mailer.domain.ApplicationCommand
import net.zhu4.mailer.domain.ApplicationCommandOption
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UploadApplicationCommandsUseCaseImpl(
    private val client: GatewayDiscordClient,
    private val persistencePort: ApplicationCommandPersistencePort
) : UploadApplicationCommandsUseCase {

    @Value("\${discord.applicationId}")
    private var applicationId: Long? = null

    override fun uploadApplicationCommands(): Mono<List<ApplicationCommand>> {
        return persistencePort.save(
            persistencePort.findNotRegistered()
                .registerCommands()
        )
            .collectList()
    }

    private fun Flux<ApplicationCommand>.registerCommands(): Flux<ApplicationCommand> {
        return this.flatMap { command ->
            if (command.guildId == null) {
                client.restClient.applicationService
                    .createGlobalApplicationCommand(applicationId!!, command.toRequest())
                    .map { command.copy(registered = true) }
            } else {
                client.restClient.applicationService
                    .createGuildApplicationCommand(applicationId!!, command.guildId, command.toRequest())
                    .map { command.copy(registered = true) }
            }
        }
    }

    companion object {

        private fun ApplicationCommand.toRequest(): ApplicationCommandRequest {
            return ApplicationCommandRequest.builder()
                .name(this.name)
                .description(this.description)
                .options(this.options.map(UploadApplicationCommandsUseCaseImpl::toData))
                .build()
        }

        private fun toData(option: ApplicationCommandOption): ApplicationCommandOptionData {
            return ApplicationCommandOptionData.builder()
                .name(option.name)
                .description(option.description)
                .type(option.type)
                .required(option.required)
                .build()
        }
    }
}
