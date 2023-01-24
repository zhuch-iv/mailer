package net.zhu4.mailer.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import java.util.*

@Configuration
class WebClientConfiguration {

    @Bean
    @Primary
    fun webClient(): WebClient {
        return WebClient.create()
    }

    @Bean("esiWebClient")
    fun esiWebClient(@Value("\${eve.baseUrl}") baseUrl: String): WebClient {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
                .defaultUriVariables(mapOf("datasource" to "tranquility"))
                .build()
    }

    @Bean("eveOauthWebClient")
    fun eveOauthWebClient(@Value("\${eve.loginUrl}") loginUrl: String): WebClient {
        return WebClient.builder()
            .baseUrl(loginUrl)
            .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "en")
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CACHE_CONTROL, "no-cache")
            .build()
    }
}
