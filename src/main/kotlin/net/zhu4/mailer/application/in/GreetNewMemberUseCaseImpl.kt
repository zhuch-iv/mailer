package net.zhu4.mailer.application.`in`

import com.samskivert.mustache.Mustache
import discord4j.common.util.Snowflake
import discord4j.core.event.domain.guild.MemberJoinEvent
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.channel.GuildChannel
import net.zhu4.mailer.application.out.TemplatePersistencePort
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.logging.Level

@Service
class GreetNewMemberUseCaseImpl(
    private val compiler: Mustache.Compiler,
    private val templatePersistencePort: TemplatePersistencePort,
) : GreetNewMemberUseCase {

    override fun greet(event: MemberJoinEvent): Mono<Void> {
        return Mono.zip(
            event.guild.getChannel(),
            templatePersistencePort.findByName(templateName)
        )
            .flatMap {
                it.t1.restChannel.createMessage(
                    compiler.compile(it.t2.template)
                        .execute(createContext(event.member))
                )
            }
            .log(GreetNewMemberUseCaseImpl::class.qualifiedName, Level.FINEST)
            .then()
    }

    private fun createContext(member: Member): Map<String, String> {
        return mapOf(
            "user" to "<@!${member.id.asLong()}>",
            "recruitment" to "<@&$recruitmentId>"
        )
    }

    private fun Mono<Guild>.getChannel(): Mono<GuildChannel> {
        return this.flatMap {
            it.getChannelById(Snowflake.of(channelId))
        }
    }

    companion object {
        private const val channelId = 775665634658091042L
        private const val recruitmentId = 1067191406692675595L
        private const val templateName = "greeting"
    }
}
