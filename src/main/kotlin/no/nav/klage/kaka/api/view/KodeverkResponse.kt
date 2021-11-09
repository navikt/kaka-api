package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kodeverk.*

data class KodeverkResponse(
    val partIdType: List<KodeDto> = PartIdType.values().asList().toDto(),
    val sakstype: List<KodeDto> = Sakstype.values().asList().toDto(),
    val tema: List<TemaKode> = getTemaer(),
    val utfall: List<KodeDto> = Utfall.values().asList().toDto(),
    val hjemmel: List<KodeDto> = Hjemmel.values().asList().toDto(),
)

fun getTemaer(): List<TemaKode> =
    Tema.values().map { tema ->
        TemaKode(
            id = tema.id,
            navn = tema.navn,
            beskrivelse = tema.beskrivelse,
            hjemler = hjemlerPerTema[tema]?.map { it.toDto() } ?: emptyList(),
            enheter = enheterPerTema[tema]?.map { it.toDto() } ?: emptyList(),
        )
    }

data class TemaKode(
    val id: String,
    val navn: String,
    val beskrivelse: String,
    val hjemler: List<KodeDto>,
    val enheter: List<KodeDto>,
)

data class KodeDto(val id: String, val navn: String, val beskrivelse: String)

fun Kode.toDto() = KodeDto(id, navn, beskrivelse)

fun List<Kode>.toDto() = map { it.toDto() }
