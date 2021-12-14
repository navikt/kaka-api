package no.nav.klage.kaka.api


import io.swagger.annotations.Api
import no.nav.klage.kaka.api.view.KodeverkResponse
import no.nav.klage.kaka.api.view.UserData
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Api(tags = ["kaka-api:metadata"])
@Unprotected
@RequestMapping("/metadata")
class MetadataController(private val tokenUtil: TokenUtil) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/kodeverk", produces = ["application/json"])
    fun getKodeverk(): KodeverkResponse {
        return KodeverkResponse()
    }

    @GetMapping("/userdata", produces = ["application/json"])
    fun getUserData(): UserData {
        return UserData(
            ident = tokenUtil.getIdent(),
            navn = tokenUtil.getName().toNavn()
        )
    }

    //Until we start using AD etc.
    private fun String.toNavn(): UserData.Navn {
        //based on: Navnesen, Navn
        val nameParts = this.split(", ")
        return if (nameParts.size > 1) {
            UserData.Navn(
                fornavn = nameParts.last(),
                etternavn = nameParts.first(),
                sammensattNavn = nameParts.last() + " " + nameParts.first()
            )
        } else {
            UserData.Navn(
                sammensattNavn = this
            )
        }
    }
}