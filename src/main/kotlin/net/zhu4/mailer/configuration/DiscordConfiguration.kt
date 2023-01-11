package net.zhu4.mailer.configuration

import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.Event
import discord4j.rest.RestClient
import net.zhu4.mailer.adapter.discord.DiscordEventListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordConfiguration {

    @Bean
    fun <T : Event> gatewayDiscordClient(
        @Value("\${mailer.discord.token}") token: String,
        eventListeners: List<DiscordEventListener<T>>
    ): GatewayDiscordClient {
        val client = DiscordClientBuilder.create(token)
                .build()
                .login()
                .block()!!
        for (listener in eventListeners) {
            client.on(listener.getEventType())
                    .flatMap(listener::execute)
                    .onErrorResume(listener::handleError)
                    .subscribe()
        }
        return client
    }

    @Bean
    fun discordRestClient(client: GatewayDiscordClient): RestClient {
        return client.restClient
    }
}
