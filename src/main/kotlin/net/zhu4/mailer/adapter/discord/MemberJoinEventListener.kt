package net.zhu4.mailer.adapter.discord

import discord4j.core.event.domain.guild.MemberJoinEvent
import net.zhu4.mailer.application.`in`.GreetNewMemberUseCase
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MemberJoinEventListener(
    private val useCase: GreetNewMemberUseCase
) : DiscordEventListener<MemberJoinEvent> {

    override fun getEventType(): Class<MemberJoinEvent> {
        return MemberJoinEvent::class.java
    }

    override fun execute(event: MemberJoinEvent): Mono<Void> {
        log.debug("Received event: $event")
        return useCase.greet(event)
    }

    companion object {
        private val log = LoggerFactory.getLogger(MemberJoinEventListener::class.java)
    }
}
