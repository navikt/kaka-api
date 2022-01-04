package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.KodeverkResponse
import no.nav.klage.kaka.api.view.getYtelser
import org.springframework.stereotype.Service

@Service
class KodeverkResponseService {

    fun getKodeVerkResponse(include2103: Boolean = false): KodeverkResponse {
        return KodeverkResponse(
            ytelser = getYtelser(include2103)
        )
    }
}