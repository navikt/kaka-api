package no.nav.klage.kaka.util

import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.domain.kodeverk.Role
import no.nav.klage.kaka.domain.kodeverk.Role.*
import no.nav.klage.kodeverk.klageenheter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RolleMapper(
    private val azureGateway: AzureGateway,
    @Value("\${KAKA_KVALITETSVURDERING_ROLE_ID}") private val kakaKvalitetsvurderingRoleId: String,
    @Value("\${KAKA_KVALITETSTILBAKEMELDING_ROLE_ID}") private val kakaKvalitetstilbakemeldingerRoleId: String,
    @Value("\${KAKA_TOTALSTATISTIKK_ROLE_ID}") private val kakaTotalstatistikkRoleId: String,
    @Value("\${KAKA_LEDERSTATISTIKK_ROLE_ID}") private val kakaLederstatistikkRoleId: String,

    @Value("\${EGEN_ANSATT_ROLE_ID}") private val egenAnsattRoleId: String,
    @Value("\${FORTROLIG_ROLE_ID}") private val fortroligRoleId: String,
    @Value("\${STRENGT_FORTROLIG_ROLE_ID}") private val strengtFortroligRoleId: String,

    @Value("\${KABAL_ADMIN_ROLE_ID}") private val adminRoleId: String,

    //TODO: Sjekk om viktig.
    @Value("\${ROLE_KLAGE_LEDER}") private val klageLederRole: String,
) {
    private val rolleMap = mapOf(
        kakaKvalitetsvurderingRoleId to setOf(ROLE_KAKA_KVALITETSVURDERING),
        kakaKvalitetstilbakemeldingerRoleId to setOf(ROLE_KAKA_KVALITETSTILBAKEMELDINGER),
        kakaTotalstatistikkRoleId to setOf(ROLE_KAKA_TOTALSTATISTIKK),
        kakaLederstatistikkRoleId to setOf(ROLE_KAKA_LEDERSTATISTIKK),

        egenAnsattRoleId to setOf(ROLE_KLAGE_EGEN_ANSATT, EGEN_ANSATT),
        fortroligRoleId to setOf(ROLE_KLAGE_FORTROLIG, FORTROLIG),
        strengtFortroligRoleId to setOf(ROLE_KLAGE_STRENGT_FORTROLIG, STRENGT_FORTROLIG),

        adminRoleId to setOf(ROLE_ADMIN),

        //TODO: Dette er samme uuid som KABAL_INNSYN_EGEN_ENHET_ROLE_ID. Overflødig her?
        klageLederRole to setOf(ROLE_KLAGE_LEDER),
    )

    fun toRoles(roleIdList: List<String>): Set<Role> {
        val roles = roleIdList.mapNotNull { rolleMap[it] }.flatten().toMutableSet()

        //TODO: Review all special handling after 2022-04-01

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