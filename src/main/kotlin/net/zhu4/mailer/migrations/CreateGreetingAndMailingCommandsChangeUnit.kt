package net.zhu4.mailer.migrations

import com.mongodb.client.result.InsertManyResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.*
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync
import net.zhu4.mailer.application.`in`.HandleAuthEventUseCase
import net.zhu4.mailer.application.`in`.HandleGreetingEventUseCase
import net.zhu4.mailer.application.`in`.HandleMailEventUseCase
import net.zhu4.mailer.domain.ApplicationCommand
import net.zhu4.mailer.domain.ApplicationCommandOption
import net.zhu4.mailer.domain.Interaction
import net.zhu4.mailer.domain.User
import org.bson.Document


@ChangeUnit(id = "create-greeting-and-mailing-commands", order = "2", author = "zhuch")
class CreateGreetingAndMailingCommandsChangeUnit {

    @BeforeExecution
    fun beforeExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection("auth").drop().subscribe(subscriber)
        subscriber.await()
        mongoDatabase.createCollection(ApplicationCommand.COLLECTION_NAME).subscribe(subscriber)
        subscriber.await()
    }

    @RollbackBeforeExecution
    fun rollbackBeforeExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.createCollection("auth").subscribe(subscriber)
        subscriber.await()
        mongoDatabase.getCollection(ApplicationCommand.COLLECTION_NAME).drop().subscribe(subscriber)
        subscriber.await()
    }

    @Execution
    fun execution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        insertCommands(mongoDatabase)
        updateUsers(mongoDatabase)
    }

    @RollbackExecution
    fun rollbackExecution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        // pass
    }

    private fun updateUsers(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<UpdateResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(User.COLLECTION_NAME, User::class.java)
            .updateMany(
                Document("interactions", Document("\$exists", false)),
                Document("\$set", Document("interactions", mapOf<String, Interaction>()))
            )
            .subscribe(subscriber)
        subscriber.await()
    }

    private fun insertCommands(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<InsertManyResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(ApplicationCommand.COLLECTION_NAME, ApplicationCommand::class.java)
            .insertMany(commands())
            .subscribe(subscriber)
        subscriber.await()
    }

    private fun commands(): List<ApplicationCommand> =
        listOf(
            ApplicationCommand(
                name = HandleGreetingEventUseCase.COMMAND_NAME,
                description = "Changes the newbies greeting template.",
                options = listOf(
                    ApplicationCommandOption(
                        "template",
                        "greeting template",
                        3,
                        listOf(),
                        false
                    ),
                    ApplicationCommandOption(
                        "off",
                        "Turns off greeting.",
                        5,
                        listOf(),
                        false
                    )
                )
            ),
            ApplicationCommand(
                name = HandleMailEventUseCase.COMMAND_NAME,
                description = "Bot mailing recruit letters newbies, if you are authorized.",
                options = listOf(
                    ApplicationCommandOption(
                        "file",
                        "File with names.",
                        11,
                        listOf(),
                        true
                    )
                )
            ),
            ApplicationCommand(
                name = HandleAuthEventUseCase.COMMAND_NAME,
                description = "Authorizes you in the bot using the EVE Online Oauth2 API.",
                options = listOf()
            ),
        )
}
