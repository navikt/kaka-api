package no.nav.klage.kaka.api


import no.nav.klage.kaka.api.view.KodeverkResponse
import no.nav.klage.kaka.util.getLogger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/metadata")
class KodeverkController {
    
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/kodeverk", produces = ["application/json"])
    fun getKodeverk(): KodeverkResponse {

        return KodeverkResponse()
    }
}