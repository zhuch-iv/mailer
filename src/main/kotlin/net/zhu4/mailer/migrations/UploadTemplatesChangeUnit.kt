package net.zhu4.mailer.migrations

import com.mongodb.client.result.InsertManyResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.*
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync
import net.zhu4.mailer.domain.Template
import org.bson.types.ObjectId

@ChangeUnit(id = "upload-templates", order = "4", author = "zhuch")
class UploadTemplatesChangeUnit {

    @BeforeExecution
    fun beforeExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection(Template.COLLECTION_NAME).drop().subscribe(subscriber)
        subscriber.await()
    }

    @RollbackBeforeExecution
    fun rollbackBeforeExecution(mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<Void> = MongoSubscriberSync()
        mongoDatabase.getCollection(Template.COLLECTION_NAME).drop().subscribe(subscriber)
        subscriber.await()
    }

    @Execution
    fun execution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<InsertManyResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(Template.COLLECTION_NAME, Template::class.java)
            .insertMany(
                listOf(
                    Template(
                        id = ObjectId(),
                        name = "subject",
                        template = "Нужна компания?"
                    ),
                    Template(
                        id = ObjectId(),
                        name = "mail",
                        template = "<font size=\"13\" color=\"#ffbfbfbf\"></font><font size=\"18\" color=\"#ffb2b2b2\"><b>Привет!</b><br><br></font><font size=\"14\" color=\"#ffb2b2b2\">Вы получили приглашение в корпорацию </font><font size=\"14\" color=\"#ffd98d00\"><a href=\"showinfo:2//98688618\">Community for Beginners</a></font><font size=\"14\" color=\"#ffb2b2b2\"> или \"</font><font size=\"14\" color=\"#ffffff00\"><b>Собщество для новичков</b></font><font size=\"14\" color=\"#ffb2b2b2\">\". <br><br>Главной целью нашего сообщества является предоставление новичку разных возможностей в игровом мире! И только сам человек решает как реализовывать эти возможности!<b> У нас лампово, не употребляют много мата</b> и <b>уважают друг друга</b>. Если у тебя иной стиль общения, тогда тебе не к нам!<br>Мы занимаемся разными направлениями в EVE Online и поможем вам начать и разобраться в каждом!<br><br><br></font><font size=\"18\" color=\"#ffb2b2b2\"><b>Что мы можем предложить?<br><br></font><font size=\"14\" color=\"#ffb2b2b2\">- Коллекция актуальных гайдов в нашем </font><font size=\"14\" color=\"#ffffe400\"><a href=\"https://discord.gg/qfDQsgrHH6\">ДИСКОРДЕ!</a><br></font><font size=\"14\" color=\"#ffb2b2b2\">- Своевременная помощь советом по игре!<br>- Возможность жить в НПС нулях </font><font size=\"14\" color=\"#ffd98d00\"><a href=\"showinfo:3//10000015\">Venal*</a></font><font size=\"14\" color=\"#ffb2b2b2\"> и выполнять вкуснейшие миссии в игре!<br>- Возможность зарабатывать 100кк isk в час </font><font size=\"14\" color=\"#ffffff00\"><i>НА АЛЬФА АККАУНТЕ </font><font size=\"14\" color=\"#ffb2b2b2\">в нашей ВХ. </i><br>- Получить сообщество, которое лояльно относится к новичкам и уважает друг друга!</b><br><br><br>Можешь зайти в наш дискорд почитать и посмотреть ОБЗОРНЫЕ гайды. Они достпны для всех!<br><br></font><font size=\"18\" color=\"#ffb2b2b2\"><b>Как подать заявку?</b><br><br></font><font size=\"14\" color=\"#ffb2b2b2\">1. Открываете корпорацию по ссылкам выше, в которую хотите вступить. Внизу справа есть кнопка \"Подать заявку на вступление\". <br>2. Дальше нужно зайти в наш дискорд, </font><font size=\"14\" color=\"#ffffff00\"><b>ПРОЙТИ СОБЕСЕДОВАНИЕ</b></font><font size=\"14\" color=\"#ffb2b2b2\"> и получить доступы на все каналы в наш </font><font size=\"14\" color=\"#ffffe400\"><a href=\"https://discord.gg/qfDQsgrHH6\">ДИСКОРД!</a></font><font size=\"14\" color=\"#ffb2b2b2\"> Мы заинтересованы только в тех игроках, которые готовы общаться!<br>3. После принятия, вступление нужно подтвердить в  </font><font size=\"14\" color=\"#ff94ccff\"><a href=\"helpPointer:unique_UI_myApplications\">Мои заявки на вступление в корпорацию</a></font><font size=\"14\" color=\"#ffb2b2b2\">. <br><br></font><font size=\"18\" color=\"#ffffff00\"><b><i>Заходи! Спрашивай! Вступай!</b></i></font>\n"
                    ),
                    Template(
                        id = ObjectId(),
                        name = "greeting",
                        template = "Привет {{{user}}}, интересует наша корпорация?\nДля вступления нужно пройти собеседование с {{{recruitment}}}"
                    )
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
