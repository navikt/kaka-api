package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Enhet
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Hjemmel
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.hjemmel.ytelseTilRegistreringshjemler

data class KodeverkResponse(
    val partIdTyper: List<KodeDto> = PartIdType.values().asList().toDto(),
    val sakstyper: List<KodeDto> = Type.values().asList().toDto(),
    val ytelser: List<YtelseKode> = getYtelser(),
    val utfall: List<KodeDto> = Utfall.values().asList().toDto(),
    val hjemler: List<KodeDto> = Hjemmel.values().asList().toDto(),
    val enheter: List<KodeDto> = Enhet.values().asList().toDto(),
)

fun getYtelser(): List<YtelseKode> =
    Ytelse.values().map { ytelse ->
        YtelseKode(
            id = ytelse.id,
            navn = ytelse.navn,
            beskrivelse = ytelse.beskrivelse,
            hjemler = ytelseToHjemler[ytelse] ?: emptyList(),
            enheter = enheterPerYtelse[ytelse]?.map { it.toDto() } ?: emptyList(),
        )
    }

val ytelseToHjemler: Map<Ytelse, List<KodeDto>> = mapOf(
    Ytelse.HJE_HJE to
            ytelseTilRegistreringshjemler.get(Ytelse.HJE_HJE)!!.map {
                it.toDto()
            },
    Ytelse.OMS_OMP to
            (Hjemmel.values().filter { it.kapittelOgParagraf != null && it.kapittelOgParagraf!!.kapittel == 9 }
            + Hjemmel.FTL + Hjemmel.MANGLER).toDto(),
    Ytelse.OMS_OLP to
            (Hjemmel.values().filter { it.kapittelOgParagraf != null && it.kapittelOgParagraf!!.kapittel == 9 }
            + Hjemmel.FTL + Hjemmel.MANGLER).toDto(),
    Ytelse.OMS_PLS to
            (Hjemmel.values().filter { it.kapittelOgParagraf != null && it.kapittelOgParagraf!!.kapittel == 9 }
            + Hjemmel.FTL + Hjemmel.MANGLER).toDto(),
    Ytelse.OMS_PSB to
            (Hjemmel.values().filter { it.kapittelOgParagraf != null && it.kapittelOgParagraf!!.kapittel == 9 }
            + Hjemmel.FTL + Hjemmel.MANGLER).toDto()
)

val enheterPerYtelse: Map<Ytelse, List<Kode>> = Ytelse.values().associateWith {
    listOf(Enhet.NAV_MOSS, Enhet.NAV_XXXX, Enhet.NAV_YYYY)
}

//val hjemlerPerYtelse: Map<Ytelse, List<Hjemmel>> = mapOf(
//    Ytelse.OMS_OMP to
//            Hjemmel.values().filter { it.kapittelOgParagraf != null && it.kapittelOgParagraf!!.kapittel == 9 }
//            + Hjemmel.FTL + Hjemmel.MANGLER,
//    Ytelse.OMS_OLP to
//            Hjemmel.values().filter { it.kapittelOgParagraf != null && it.kapittelOgParagraf!!.kapittel == 9 }
//            + Hjemmel.FTL + Hjemmel.MANGLER,
//    Ytelse.OMS_PLS to
//            Hjemmel.values().filter { it.kapittelOgParagraf != null && it.kapittelOgParagraf!!.kapittel == 9 }
//            + Hjemmel.FTL + Hjemmel.MANGLER,
//    Ytelse.OMS_PSB to
//            Hjemmel.values().filter { it.kapittelOgParagraf != null && it.kapittelOgParagraf!!.kapittel == 9 }
//            + Hjemmel.FTL + Hjemmel.MANGLER
//)

data class YtelseKode(
    val id: String,
    val navn: String,
    val beskrivelse: String,
    val hjemler: List<KodeDto>,
    val enheter: List<KodeDto>,
)

data class KodeDto(val id: String, val navn: String, val beskrivelse: String)

fun Registreringshjemmel.toDto() = KodeDto(
    id = id,
    navn = lovKilde.navn + " - " + spesifikasjon,
    beskrivelse = lovKilde.kortform + " - " + spesifikasjon
)

fun Kode.toDto() = KodeDto(id, navn, beskrivelse)

fun List<Kode>.toDto() = map { it.toDto() }
