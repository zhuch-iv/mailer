package net.zhu4.mailer.migrations

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.*
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync
import net.zhu4.mailer.domain.User
import org.bson.types.ObjectId

@ChangeUnit(id = "add-user", order = "3", author = "zhuch")
class AddUserChangeUnit {

    @Execution
    fun execution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<InsertOneResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(User.COLLECTION_NAME, User::class.java)
            .insertOne(
                clientSession,
                User(ObjectId(), 414061635590946826, true)
            )
            .subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun rollbackExecution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<DeleteResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(User.COLLECTION_NAME, User::class.java)
            .deleteOne(
                clientSession,
                eq("discordId", 414061635590946826)
            )
            .subscribe(subscriber)
        subscriber.await()
    }
}
