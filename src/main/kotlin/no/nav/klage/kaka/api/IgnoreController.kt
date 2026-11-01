package no.nav.klage.kaka.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IgnoreController {
    @GetMapping("favicon.ico")
    fun nothingToSeeHere() {
    }

    @GetMapping("ads.txt")
    fun nothingToSeeHereEither() {
    }

    @GetMapping("robots.txt")
    fun stillNothingToSeeHere() {
    }

    @GetMapping("site.webmanifest")
    fun stillNothingAtAllToSeeHere() {
    }
}
