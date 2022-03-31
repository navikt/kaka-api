package no.nav.klage.kaka.util

import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.domain.kodeverk.Role
import no.nav.klage.kaka.domain.kodeverk.Role.*
import no.nav.klage.kaka.domain.saksbehandler.SaksbehandlerRolle
import no.nav.klage.kodeverk.klageenheter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RolleMapper(
    private val azureGateway: AzureGateway,
    @Value("\${ROLE_KAKA_KVALITETSVURDERING}") private val kakaKvalitetsvurderingRole: String,
    @Value("\${ROLE_KAKA_KVALITETSTILBAKEMELDINGER}") private val kakaKvalitetstilbakemeldingerRole: String,
    @Value("\${ROLE_KAKA_TOTALSTATISTIKK}") private val kakaTotalstatistikkRole: String,
    @Value("\${ROLE_KAKA_LEDERSTATISTIKK}") private val kakaLederstatistikkRole: String,

    @Value("\${ROLE_KLAGE_EGEN_ANSATT}") private val kanBehandleEgenAnsattRole: String,
    @Value("\${ROLE_KLAGE_FORTROLIG}") private val kanBehandleFortroligRole: String,
    @Value("\${ROLE_KLAGE_STRENGT_FORTROLIG}") private val kanBehandleStrengtFortroligRole: String,

    @Value("\${ROLE_ADMIN}") private val adminRole: String,

    @Value("\${ROLE_KLAGE_LEDER}") private val klageLederRole: String,
) {
    private val rolleMap = mapOf(
        kakaKvalitetsvurderingRole to ROLE_KAKA_KVALITETSVURDERING,
        kakaKvalitetstilbakemeldingerRole to ROLE_KAKA_KVALITETSTILBAKEMELDINGER,
        kakaTotalstatistikkRole to ROLE_KAKA_TOTALSTATISTIKK,
        kakaLederstatistikkRole to ROLE_KAKA_LEDERSTATISTIKK,

        kanBehandleEgenAnsattRole to ROLE_KLAGE_EGEN_ANSATT,
        kanBehandleFortroligRole to ROLE_KLAGE_FORTROLIG,
        kanBehandleStrengtFortroligRole to ROLE_KLAGE_STRENGT_FORTROLIG,

        adminRole to ROLE_ADMIN,

        klageLederRole to ROLE_KLAGE_LEDER,
    )

    fun toRoles(roleIdList: List<SaksbehandlerRolle>): Set<Role> {
        val roles = roleIdList.mapNotNull { rolleMap[it.id] }.toMutableSet()

        //TODO: Remove all special handling after 2022-04-01

        //give FE old names for roles for compatibility.
        if (ROLE_KAKA_KVALITETSVURDERING in roles) {
            roles += ROLE_KAKA_SAKSBEHANDLER
        }
        if (ROLE_KAKA_KVALITETSTILBAKEMELDINGER in roles) {
            roles += ROLE_VEDTAKSINSTANS_LEDER
        }

        if (ROLE_KLAGE_LEDER in roles) {
            if (azureGateway.getDataOmInnloggetSaksbehandler().enhet in klageenheter) {
                roles += ROLE_KAKA_LEDERSTATISTIKK
            }
            roles += ROLE_KAKA_TOTALSTATISTIKK
        }

        return roles
    }

}

fun isAllowedToReadKvalitetstilbakemeldinger(roller: Set<Role>): Boolean {
    return ROLE_KAKA_KVALITETSTILBAKEMELDINGER in roller
}