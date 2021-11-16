package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Enhet
import no.nav.klage.kaka.domain.kodeverk.*

data class KodeverkResponse(
    val partIdTyper: List<KodeDto> = PartIdType.values().asList().toDto(),
    val sakstyper: List<KodeDto> = Sakstype.values().asList().toDto(),
    val temaer: List<TemaKode> = getTemaer(),
    val ytelser: List<YtelseKode> = getYtelser(),
    val utfall: List<KodeDto> = Utfall.values().asList().toDto(),
    val hjemler: List<KodeDto> = Hjemmel.values().asList().toDto(),
    val enheter: List<KodeDto> = Enhet.values().asList().toDto(),
)

fun getTemaer(): List<TemaKode> =
    Tema.values().map { tema ->
        TemaKode(
            id = tema.id,
            navn = tema.navn,
            beskrivelse = tema.beskrivelse,
            hjemler = hjemlerPerTema[tema]?.map { it.toDto() } ?: emptyList(),
            vedtaksenheter = enheterPerTema[tema]?.map { it.toDto() } ?: emptyList(),
        )
    }

fun getYtelser(): List<YtelseKode> =
    Ytelse.values().map { ytelse ->
        YtelseKode(
            id = ytelse.id,
            navn = ytelse.navn,
            beskrivelse = ytelse.beskrivelse,
            hjemler = hjemlerPerYtelse[ytelse]?.map { it.toDto() } ?: emptyList(),
            enheter = enheterPerYtelse[ytelse]?.map { it.toDto() } ?: emptyList(),
        )
    }

data class TemaKode(
    val id: String,
    val navn: String,
    val beskrivelse: String,
    val hjemler: List<KodeDto>,
    val vedtaksenheter: List<KodeDto>,
)

data class YtelseKode(
    val id: String,
    val navn: String,
    val beskrivelse: String,
    val hjemler: List<KodeDto>,
    val enheter: List<KodeDto>,
)

data class KodeDto(val id: String, val navn: String, val beskrivelse: String)

fun Kode.toDto() = KodeDto(id, navn, beskrivelse)

fun List<Kode>.toDto() = map { it.toDto() }
