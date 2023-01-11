package net.zhu4.mailer.migrations

import com.mongodb.client.result.InsertManyResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.*
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync
import net.zhu4.mailer.domain.Character
import net.zhu4.mailer.domain.Corporation
import net.zhu4.mailer.domain.EveAuthorization


@ChangeUnit(id = "database-initialize", order = "1", author = "zhuch")
class DatabaseInitializeChangeUnit {

    @BeforeExecution
    fun beforeExecution(mongoDatabase: MongoDatabase) {
        // https://github.com/mongock/mongock-examples/blob/master/mongodb/springboot-reactive/src/main/java/io/mongock/examples/mongodb/springboot/reactive/migration/ClientInitializerChangeUnit.java
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.createCollection(Character.COLLECTION_NAME).subscribe(subscriber)
        mongoDatabase.createCollection(Corporation.COLLECTION_NAME).subscribe(subscriber)
        mongoDatabase.createCollection(EveAuthorization.COLLECTION_NAME).subscribe(subscriber)
        subscriber.await()
    }

    @RollbackBeforeExecution
    fun rollbackBeforeExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection(Character.COLLECTION_NAME).drop().subscribe(subscriber)
        mongoDatabase.getCollection(Corporation.COLLECTION_NAME).drop().subscribe(subscriber)
        mongoDatabase.getCollection(EveAuthorization.COLLECTION_NAME).drop().subscribe(subscriber)
        subscriber.await()
    }

    @Execution
    fun execution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<InsertManyResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(Corporation.COLLECTION_NAME, Corporation::class.java)
            .insertMany(
                listOf(
                    Corporation(1000165, "Hedion University", true),
                    Corporation(1000166, "Imperial Academy", true),
                    Corporation(1000077, "Royal Amarr Institute", true),

                    Corporation(1000044, "School of Applied Knowledge", true),
                    Corporation(1000045, "Science and Trade Institute", true),
                    Corporation(1000167, "State War Academy", true),

                    Corporation(1000169, "Center for Advanced Studies", true),
                    Corporation(1000168, "Federal Navy Academy", true),
                    Corporation(1000115, "University of Caille", true),

                    Corporation(1000172, "Pator Tech School", true),
                    Corporation(1000170, "Republic Military School", true),
                    Corporation(1000171, "Republic University", true),
                )
            )
            .subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun rollbackExecution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        // pass
    }
}
