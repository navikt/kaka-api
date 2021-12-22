package no.nav.klage.kaka.api.view

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RolleMapper(
    @Value("\${ROLE_GOSYS_OPPGAVE_BEHANDLER}") private val gosysSaksbehandlerRole: String,
    @Value("\${ROLE_KLAGE_SAKSBEHANDLER}") private val saksbehandlerRole: String,
    @Value("\${ROLE_KLAGE_FAGANSVARLIG}") private val fagansvarligRole: String,
    @Value("\${ROLE_KLAGE_LEDER}") private val lederRole: String,
    @Value("\${ROLE_KLAGE_MERKANTIL}") private val merkantilRole: String,
    @Value("\${ROLE_KLAGE_FORTROLIG}") private val kanBehandleFortroligRole: String,
    @Value("\${ROLE_KLAGE_STRENGT_FORTROLIG}") private val kanBehandleStrengtFortroligRole: String,
    @Value("\${ROLE_KLAGE_EGEN_ANSATT}") private val kanBehandleEgenAnsattRole: String,
    @Value("\${ROLE_ADMIN}") private val adminRole: String
) {
    val rolleMap = mapOf(
        gosysSaksbehandlerRole to "ROLE_GOSYS_OPPGAVE_BEHANDLER",
        saksbehandlerRole to "ROLE_KLAGE_SAKSBEHANDLER",
        fagansvarligRole to "ROLE_KLAGE_FAGANSVARLIG",
        lederRole to "ROLE_KLAGE_LEDER",
        merkantilRole to "ROLE_KLAGE_MERKANTIL",
        kanBehandleFortroligRole to "ROLE_KLAGE_FORTROLIG",
        kanBehandleStrengtFortroligRole to "ROLE_KLAGE_STRENGT_FORTROLIG",
        kanBehandleEgenAnsattRole to "ROLE_KLAGE_EGEN_ANSATT",
        adminRole to "ROLE_ADMIN",
    )
}