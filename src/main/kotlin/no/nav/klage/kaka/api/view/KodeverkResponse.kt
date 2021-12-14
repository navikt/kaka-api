package no.nav.klage.kaka.api.view

import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Hjemmel
import no.nav.klage.kodeverk.hjemmel.LovKilde
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.hjemmel.ytelseTilRegistreringshjemler

data class KodeverkResponse(
    val partIdTyper: List<KodeDto> = PartIdType.values().asList().toDto(),
    val sakstyper: List<KodeDto> = Type.values().asList().toDto(),
    val ytelser: List<YtelseKode> = getYtelser(),
    val utfall: List<KodeDto> = Utfall.values().asList().toDto(),
    val registreringshjemler: List<KodeDto> = getRegistreringshjemler(),
    val enheter: List<KodeDto> = Enhet.values().asList().toDto(),
    val klageenheter: List<KlageenhetKode> = getKlageenheter(),
    val lovKilder: List<KodeDto> = LovKilde.values().asList().toDto(),
)

fun getKlageenheter(): List<KlageenhetKode> {
    val klageenheter = Enhet.values().filter { it.navn.startsWith("42") }
    return klageenheter.map { klageenhet ->
        KlageenhetKode(
            id = klageenhet.id,
            navn = klageenhet.navn,
            beskrivelse = klageenhet.beskrivelse,
            ytelser = ytelseTilKlageenheter.filter { klageenhet.navn in it.value.map { e -> e.navn } }.map {
                it.key.toDto()
            }.toSet()
        )
    }
}

fun getYtelser(): List<YtelseKode> =
    Ytelse.values().map { ytelse ->
        YtelseKode(
            id = ytelse.id,
            navn = ytelse.navn,
            beskrivelse = ytelse.beskrivelse,
            hjemler = ytelseToHjemler[ytelse] ?: emptyList(),
            lovKildeToRegistreringshjemler = ytelseToLovKildeToRegistreringshjemmel[ytelse] ?: emptyList(),
            enheter = ytelseTilVedtaksenheter[ytelse]?.map { it.toDto() } ?: emptyList(),
            klageenheter = ytelseTilKlageenheter[ytelse]?.map { it.toDto() } ?: emptyList(),
        )
    }

fun getRegistreringshjemler(): List<KodeDto> =
    Registreringshjemmel.values().map {
        KodeDto(
            id = it.id,
            navn = it.lovKilde.beskrivelse + " - " + it.spesifikasjon,
            beskrivelse = it.lovKilde.navn + " - " + it.spesifikasjon,
        )
    }

val ytelseToLovKildeToRegistreringshjemmel: Map<Ytelse, List<LovKildeToRegistreringshjemler>> =
    ytelseTilRegistreringshjemler.mapValues { (_, hjemler) ->
        hjemler.groupBy (
            { hjemmel -> hjemmel.lovKilde},
            { hjemmel -> HjemmelDto(hjemmel.id, hjemmel.spesifikasjon)}
        ).map { hjemmel ->
            LovKildeToRegistreringshjemler(
                hjemmel.key.toDto(),
                hjemmel.value
            )
        }
    }

val ytelseToHjemler: Map<Ytelse, List<KodeDto>> = mapOf(
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

data class YtelseKode(
    val id: String,
    val navn: String,
    val beskrivelse: String,
    val hjemler: List<KodeDto>,
    val lovKildeToRegistreringshjemler: List<LovKildeToRegistreringshjemler>,
    val enheter: List<KodeDto>,
    val klageenheter: List<KodeDto>,
)

data class KlageenhetKode(
    val id: String,
    val navn: String,
    val beskrivelse: String,
    val ytelser: Set<KodeDto>,
)

data class HjemmelDto(val id: String, val navn: String)

data class KodeDto(val id: String, val navn: String, val beskrivelse: String)

data class LovKildeToRegistreringshjemler(val lovkilde: KodeDto, val registreringshjemler: List<HjemmelDto>)

fun Kode.toDto() = KodeDto(id, navn, beskrivelse)

fun List<Kode>.toDto() = map { it.toDto() }
