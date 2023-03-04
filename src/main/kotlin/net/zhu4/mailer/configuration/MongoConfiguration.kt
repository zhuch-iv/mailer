package net.zhu4.mailer.configuration

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import io.mongock.driver.mongodb.reactive.driver.MongoReactiveDriver
import io.mongock.runner.springboot.MongockSpringboot
import io.mongock.runner.springboot.base.MongockInitializingBeanRunner
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Configuration
class MongoConfiguration {

    @Bean
    fun getBuilder(@Value("\${spring.data.mongodb.database}") dataBaseName: String,
            reactiveMongoClient: MongoClient,
            context: ApplicationContext
    ): MongockInitializingBeanRunner? {
        return MongockSpringboot.builder()
                .setDriver(MongoReactiveDriver.withDefaultLock(reactiveMongoClient, dataBaseName))
                .addMigrationScanPackage("net.zhu4.mailer.migrations")
                .setSpringContext(context)
                .setTransactionEnabled(false)
                .buildInitializingBeanRunner()
    }

    @Bean
    fun mongoClient(@Value("\${spring.data.mongodb.uri}") mongoConnectionString: String): MongoClient {
        val codecRegistry: CodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()))
        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(mongoConnectionString))
                .codecRegistry(codecRegistry)
                .build())
    }

}

@WritingConverter
class LocalDateTimeWritingConverter : Converter<LocalDateTime, Date> {

    override fun convert(source: LocalDateTime): Date {
        return Date.from(source.atZone(ZoneId.systemDefault())
            .toInstant())
    }
}

@ReadingConverter
class LocalDateTimeReadingConverter : Converter<Date, LocalDateTime> {

    override fun convert(source: Date): LocalDateTime {
        return source.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }
}
