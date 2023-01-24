package net.zhu4.mailer.migrations

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates.set
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.*
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync
import net.zhu4.mailer.domain.Template

@ChangeUnit(id = "update-template", order = "5", author = "zhuch")
class UpdateTemplateChangeUnit {

    @Execution
    fun execution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<UpdateResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(Template.COLLECTION_NAME, Template::class.java)
            .updateOne(
                eq("name", "greeting"),
                set("template", "{{{user}}} привет, ищешь компанию? Чтобы к нам присоединится нужно пройти собеседование.\nЛинкуй {{{recruitment}}} и тебе ответят)")
            )
            .subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun rollbackExecution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<UpdateResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(Template.COLLECTION_NAME, Template::class.java)
            .updateOne(
                eq("name", "greeting"),
                set("template", "Привет {{{user}}}, интересует наша корпорация?\nДля вступления нужно пройти собеседование с {{{recruitment}}}")
            )
            .subscribe(subscriber)
        subscriber.await()
    }
}
