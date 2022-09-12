package no.nav.klage.kaka.api

import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IgnoreController {

    @Unprotected
    @GetMapping("favicon.ico")
    fun nothingToSeeHere() {
    }

    @Unprotected
    @GetMapping("ads.txt")
    fun nothingToSeeHereEither() {
    }

    @Unprotected
    @GetMapping("robots.txt")
    fun stillNothingToSeeHere() {
    }

    @Unprotected
    @GetMapping("site.webmanifest")
    fun stillNothingAtAllToSeeHere() {
    }
}