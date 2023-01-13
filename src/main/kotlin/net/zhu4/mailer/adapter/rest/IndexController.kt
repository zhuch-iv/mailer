package net.zhu4.mailer.adapter.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.result.view.Rendering
import reactor.core.publisher.Mono

@Controller
class IndexController {

    @RequestMapping("/")
    fun index(): Mono<Rendering> {
        return Mono.just(Rendering.view("index").build())
    }
}