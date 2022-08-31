package no.nav.klage.kaka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KakaApplication

fun main(args: Array<String>) {
	runApplication<KakaApplication>(*args)
}
