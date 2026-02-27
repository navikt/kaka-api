package no.nav.klage.kaka.util

import no.nav.klage.kaka.domain.kodeverk.Role
import no.nav.klage.kaka.domain.kodeverk.Role.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class RolleMapper(
    @Value("\${KAKA_KVALITETSVURDERING_ROLE_ID}") private val kakaKvalitetsvurderingRoleId: String,
    @Value("\${KAKA_KVALITETSTILBAKEMELDING_ROLE_ID}") private val kakaKvalitetstilbakemeldingerRoleId: String,
    @Value("\${KAKA_TOTALSTATISTIKK_ROLE_ID}") private val kakaTotalstatistikkRoleId: String,
    @Value("\${KAKA_LEDERSTATISTIKK_ROLE_ID}") private val kakaLederstatistikkRoleId: String,

    @Value("\${KAKA_EXCEL_UTTREKK_MED_FRITEKST_ROLE_ID}") private val kakaExcelUttrekkMedFritekstRoleId: String,
    @Value("\${KAKA_EXCEL_UTTREKK_UTEN_FRITEKST_ROLE_ID}") private val kakaExcelUttrekkUtenFritekstRoleId: String,

    @Value("\${ADMIN_ROLE_ID}") private val adminRoleId: String,

    //TODO: Sjekk om viktig.
    @Value("\${ROLE_KLAGE_LEDER}") private val klageLederRole: String,
) {
    private val rolleMap = mapOf(
        kakaKvalitetsvurderingRoleId to setOf(KAKA_KVALITETSVURDERING),
        kakaKvalitetstilbakemeldingerRoleId to setOf(KAKA_KVALITETSTILBAKEMELDINGER),
        kakaTotalstatistikkRoleId to setOf(KAKA_TOTALSTATISTIKK),
        kakaLederstatistikkRoleId to setOf(KAKA_LEDERSTATISTIKK),

        kakaExcelUttrekkMedFritekstRoleId to setOf(KAKA_EXCEL_UTTREKK_MED_FRITEKST),
        kakaExcelUttrekkUtenFritekstRoleId to setOf(KAKA_EXCEL_UTTREKK_UTEN_FRITEKST),

        adminRoleId to setOf(KAKA_ADMIN),

        //TODO: Dette er samme uuid som KABAL_INNSYN_EGEN_ENHET_ROLE_ID. Overflødig her? Pågående diskusjon med fagsiden.
        klageLederRole to setOf(ROLE_KLAGE_LEDER, KAKA_EXCEL_UTTREKK_MED_FRITEKST),
    )

    fun toRoles(roleIdList: List<String>): Set<Role> = roleIdList.mapNotNull { rolleMap[it] }.flatten().toSet()

}

fun isAllowedToReadKvalitetstilbakemeldinger(roller: Set<Role>): Boolean {
    return KAKA_KVALITETSTILBAKEMELDINGER in roller
}