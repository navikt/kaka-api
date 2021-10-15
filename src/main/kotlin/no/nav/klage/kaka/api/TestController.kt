package no.nav.klage.kaka.api

import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Profile("local","dev-gcp")
@RestController
class TestController {

    @GetMapping("/test")
    fun testGet(): String {
        return "test"
    }
}