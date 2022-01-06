package no.nav.klage.kaka.api.view

import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.ytelseTilRegistreringshjemler

data class KodeverkResponse(
    val sakstyper: List<KodeDto> = Type.values().asList().toDto(),
    val ytelser: List<YtelseKode>,
    val utfall: List<KodeDto> = Utfall.values().asList().toDto(),
    val enheter: List<KodeDto> = Enhet.values().asList().toDto(),
    val klageenheter: List<KlageenhetKode> = getKlageenheter(),
    val sources: List<KodeDto> = Source.values().asList().toDto()
)

fun getKlageenheter(): List<KlageenhetKode> =
    klageenhetTilYtelser.map { klageenhetTilYtelse ->
        KlageenhetKode(
            id = klageenhetTilYtelse.key.id,
            navn = klageenhetTilYtelse.key.navn,
            beskrivelse = klageenhetTilYtelse.key.beskrivelse,
            ytelser = klageenhetTilYtelse.value.toDto()
        )
    }

fun getYtelser(include2103: Boolean = false): List<YtelseKode> =
    Ytelse.values().map { ytelse ->
        YtelseKode(
            id = ytelse.id,
            navn = ytelse.navn,
            beskrivelse = ytelse.beskrivelse,
            lovKildeToRegistreringshjemler = ytelseToLovKildeToRegistreringshjemmel[ytelse] ?: emptyList(),
            enheter = ytelseTilVedtaksenheter[ytelse]?.filter {
                it != Enhet.E2103 || include2103
            }?.map { it.toDto() } ?: emptyList(),
            klageenheter = ytelseTilKlageenheter[ytelse]?.filter {
                it != Enhet.E2103 || include2103
            }?.map { it.toDto() } ?: emptyList(),
        )
    }

val ytelseToLovKildeToRegistreringshjemmel: Map<Ytelse, List<LovKildeToRegistreringshjemler>> =
    ytelseTilRegistreringshjemler.mapValues { (_, hjemler) ->
        hjemler.groupBy(
            { hjemmel -> hjemmel.lovKilde },
            { hjemmel -> HjemmelDto(hjemmel.id, hjemmel.spesifikasjon) }
        ).map { hjemmel ->
            LovKildeToRegistreringshjemler(
                hjemmel.key.toDto(),
                hjemmel.value
            )
        }
    }

data class YtelseKode(
    val id: String,
    val navn: String,
    val beskrivelse: String,
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

fun Set<Kode>.toDto() = map { it.toDto() }.toSet()
