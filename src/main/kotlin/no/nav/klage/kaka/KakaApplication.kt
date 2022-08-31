package no.nav.klage.kaka

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableJwtTokenValidation(ignore = ["org.springdoc"])
class KakaApplication

fun main(args: Array<String>) {
	runApplication<KakaApplication>(*args)
}
