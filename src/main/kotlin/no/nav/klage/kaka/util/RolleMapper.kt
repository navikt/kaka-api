package no.nav.klage.kaka.util

import no.nav.klage.kaka.domain.saksbehandler.SaksbehandlerRolle
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RolleMapper(
    @Value("\${ROLE_KAKA_SAKSBEHANDLER}") private val kakaSaksbehandlerRole: String,
    @Value("\${ROLE_KLAGE_LEDER}") private val lederRole: String,
    @Value("\${ROLE_VEDTAKSINSTANS_LEDER}") private val vedtaksinstansLederRole: String,
    @Value("\${ROLE_KLAGE_EGEN_ANSATT}") private val kanBehandleEgenAnsattRole: String,
    @Value("\${ROLE_KLAGE_FORTROLIG}") private val kanBehandleFortroligRole: String,
    @Value("\${ROLE_KLAGE_STRENGT_FORTROLIG}") private val kanBehandleStrengtFortroligRole: String,
    @Value("\${ROLE_ADMIN}") private val adminRole: String,
) {
    private val rolleMap = mapOf(
        kakaSaksbehandlerRole to "ROLE_KAKA_SAKSBEHANDLER",
        lederRole to "ROLE_KLAGE_LEDER",
        kanBehandleEgenAnsattRole to "ROLE_KLAGE_EGEN_ANSATT",
        kanBehandleFortroligRole to "ROLE_KLAGE_FORTROLIG",
        kanBehandleStrengtFortroligRole to "ROLE_KLAGE_STRENGT_FORTROLIG",
        vedtaksinstansLederRole to "ROLE_VEDTAKSINSTANS_LEDER",
        adminRole to "ROLE_ADMIN",
    )

    fun toRoles(roleIdList: List<SaksbehandlerRolle>): List<String> {
        val roles = roleIdList.mapNotNull { rolleMap[it.id] }

        //stay compatible until FE removes ROLE_KLAGEINSTANS_ALLE
        if ("ROLE_KAKA_SAKSBEHANDLER" in roles) {
            return roles + "ROLE_KLAGEINSTANS_ALLE"
        }
        return roles
    }

}

fun isLederVedtaksinstans(roller: List<String>): Boolean {
    return "ROLE_VEDTAKSINSTANS_LEDER" in roller
}