package net.zhu4.mailer.migrations

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.client.result.UpdateResult
import com.mongodb.reactivestreams.client.ClientSession
import com.mongodb.reactivestreams.client.MongoDatabase
import io.mongock.api.annotations.*
import io.mongock.driver.mongodb.reactive.util.MongoSubscriberSync
import io.mongock.driver.mongodb.reactive.util.SubscriberSync
import net.zhu4.mailer.domain.Template

@ChangeUnit(id = "update-mail-template", order = "6", author = "zhuch")
class UpdateMailTemplate {

    @BeforeExecution
    fun beforeExecution(mongoDatabase: MongoDatabase) {
        // pass
    }

    @RollbackBeforeExecution
    fun rollbackBeforeExecution(mongoDatabase: MongoDatabase) {
        // pass
    }

    @Execution
    fun execution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<UpdateResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(Template.COLLECTION_NAME, Template::class.java)
            .updateOne(
                Filters.eq("name", "mail"),
                Updates.set("template", "<font size=\"13\" color=\"#bfffffff\"></font><font size=\"14\" color=\"#ffbfbfbf\">Коллектив новой крохотной корпорации ищет компаньонов! <br><br>Мы </font><font size=\"14\" color=\"#ffd98d00\"><a href=\"showinfo:2//98731976\">Chasers Tower</a></font><font size=\"14\" color=\"#ffbfbfbf\">, маленькая корпорация с дружным коллективом. Нам не хватает активных людей для более интересных активностей! Поэтому мы приглашаем тебе взглянуть на нас и, возможно, тебе у нас понравится) <br><br>Корпорация состоит в таком же молодом, но перспективном альянсе </font><font size=\"14\" color=\"#ffd98d00\"><a href=\"showinfo:1375//92571320\">Arhont Sibirskii</a></font><font size=\"14\" color=\"#ffbfbfbf\"> - </font><font size=\"14\" color=\"#ffd98d00\"><a href=\"showinfo:16159//99012019\">Can i bring my Drake...</a></font><font size=\"14\" color=\"#ffbfbfbf\"> и вы можете внести свою лепту в его развитие! Но основной дом корпорации - ВХ. <br><br>Мы приветсвуем как полных новичков, так и к более опытных пилотов. Если вы опытный пилот и любите новичковую движуху - это к нам. А новичку будет просто комфортно познавать игру в дружном маленьком коллективе, где большенство пилотов не сильно от них отличаются. К тому же у нас нет особых требований и обязательств - играйте в удовольствие! <br><br></font><font size=\"18\" color=\"#ffffffff\"><b>У нас в вы можете найти:</b><br><br></font><font size=\"14\" color=\"#ffbfbfbf\">1. Ламповый маленький коллектив;<br>2. Базу знаний в нашем </font><font size=\"14\" color=\"#ffffe400\"><a href=\"https://discord.gg/6gxqzu3rbN\">ДИСКОРДЕ</a></font><font size=\"14\" color=\"#ffbfbfbf\"> и обучающие вылеты;<br>3. Фиты и планы прокачки, которые подходят новичкам и будут полезны;<br>4. Возможность фармить на комфортную игру даже на </font><font size=\"14\" color=\"#ffffff00\"><b>АЛЬФЕ</b></font><font size=\"14\" color=\"#ffbfbfbf\"> и оплачивать </font><font size=\"14\" color=\"#ffffff00\"><b>ОМЕГУ</b></font><font size=\"14\" color=\"#ffbfbfbf\">;<br>5. Получать достаточно ПВП опыта в альянсовых и корпоративных активностях;<br><br></font><font size=\"18\" color=\"#ffffffff\"><b>А от вас требуется всего: </b><br><br></font><font size=\"14\" color=\"#ffbfbfbf\">1. Быть старше 18; <br>2. Иметь микрофон и желание активно, а главное культурно, общаться; <br>3. Наличие желания многосторонне развиватья в мире Нового Эдема.  <br><br></font><font size=\"18\" color=\"#ffbfbfbf\"><b>Мы не кусаемся, заходи в дискорд по ссылке ниже - читай, задавай вопросы, проходи собеседовние)<br></font><font size=\"18\" color=\"#ffffe400\"><loc><a href=\"https://discord.gg/6gxqzu3rbN\">https://discord.gg/6gxqzu3rbN</a></loc></b></font>")
            )
            .subscribe(subscriber)
        subscriber.await()
    }

    @RollbackExecution
    fun rollbackExecution(clientSession: ClientSession, mongoDatabase: MongoDatabase) {
        val subscriber: SubscriberSync<UpdateResult> = MongoSubscriberSync()
        mongoDatabase.getCollection(Template.COLLECTION_NAME, Template::class.java)
            .updateOne(
                Filters.eq("name", "mail"),
                Updates.set("template", "<font size=\"13\" color=\"#ffbfbfbf\"></font><font size=\"18\" color=\"#ffb2b2b2\"><b>Привет!</b><br><br></font><font size=\"14\" color=\"#ffb2b2b2\">Вы получили приглашение в корпорацию </font><font size=\"14\" color=\"#ffd98d00\"><a href=\"showinfo:2//98688618\">Community for Beginners</a></font><font size=\"14\" color=\"#ffb2b2b2\"> или \"</font><font size=\"14\" color=\"#ffffff00\"><b>Собщество для новичков</b></font><font size=\"14\" color=\"#ffb2b2b2\">\". <br><br>Главной целью нашего сообщества является предоставление новичку разных возможностей в игровом мире! И только сам человек решает как реализовывать эти возможности!<b> У нас лампово, не употребляют много мата</b> и <b>уважают друг друга</b>. Если у тебя иной стиль общения, тогда тебе не к нам!<br>Мы занимаемся разными направлениями в EVE Online и поможем вам начать и разобраться в каждом!<br><br><br></font><font size=\"18\" color=\"#ffb2b2b2\"><b>Что мы можем предложить?<br><br></font><font size=\"14\" color=\"#ffb2b2b2\">- Коллекция актуальных гайдов в нашем </font><font size=\"14\" color=\"#ffffe400\"><a href=\"https://discord.gg/qfDQsgrHH6\">ДИСКОРДЕ!</a><br></font><font size=\"14\" color=\"#ffb2b2b2\">- Своевременная помощь советом по игре!<br>- Возможность жить в НПС нулях </font><font size=\"14\" color=\"#ffd98d00\"><a href=\"showinfo:3//10000015\">Venal*</a></font><font size=\"14\" color=\"#ffb2b2b2\"> и выполнять вкуснейшие миссии в игре!<br>- Возможность зарабатывать 100кк isk в час </font><font size=\"14\" color=\"#ffffff00\"><i>НА АЛЬФА АККАУНТЕ </font><font size=\"14\" color=\"#ffb2b2b2\">в нашей ВХ. </i><br>- Получить сообщество, которое лояльно относится к новичкам и уважает друг друга!</b><br><br><br>Можешь зайти в наш дискорд почитать и посмотреть ОБЗОРНЫЕ гайды. Они достпны для всех!<br><br></font><font size=\"18\" color=\"#ffb2b2b2\"><b>Как подать заявку?</b><br><br></font><font size=\"14\" color=\"#ffb2b2b2\">1. Открываете корпорацию по ссылкам выше, в которую хотите вступить. Внизу справа есть кнопка \"Подать заявку на вступление\". <br>2. Дальше нужно зайти в наш дискорд, </font><font size=\"14\" color=\"#ffffff00\"><b>ПРОЙТИ СОБЕСЕДОВАНИЕ</b></font><font size=\"14\" color=\"#ffb2b2b2\"> и получить доступы на все каналы в наш </font><font size=\"14\" color=\"#ffffe400\"><a href=\"https://discord.gg/qfDQsgrHH6\">ДИСКОРД!</a></font><font size=\"14\" color=\"#ffb2b2b2\"> Мы заинтересованы только в тех игроках, которые готовы общаться!<br>3. После принятия, вступление нужно подтвердить в  </font><font size=\"14\" color=\"#ff94ccff\"><a href=\"helpPointer:unique_UI_myApplications\">Мои заявки на вступление в корпорацию</a></font><font size=\"14\" color=\"#ffb2b2b2\">. <br><br></font><font size=\"18\" color=\"#ffffff00\"><b><i>Заходи! Спрашивай! Вступай!</b></i></font>\n")
            )
            .subscribe(subscriber)
        subscriber.await()
    }
}
