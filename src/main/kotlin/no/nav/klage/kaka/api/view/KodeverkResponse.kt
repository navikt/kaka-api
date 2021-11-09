package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kodeverk.*

data class KodeverkResponse(
    val partIdType: List<Kode> = PartIdType.values().asList().toDto(),
    val sakstype: List<Kode> = Sakstype.values().asList().toDto(),
    val tema: List<Kode> = Tema.values().asList().toDto(),
    val utfall: List<Kode> = Utfall.values().asList().toDto(),
    val hjemmel: List<Kode> = Hjemmel.values().asList().toDto(),
    val hjemlerPerTema: List<HjemlerPerTema> = hjemlerPerTema()
)

data class KodeDto(override val id: String, override val navn: String, override val beskrivelse: String) : Kode

data class HjemlerPerTema(val temaId: String, val hjemler: List<KodeDto>)

fun Kode.toDto() = KodeDto(id, navn, beskrivelse)

fun List<Kode>.toDto() = map { it.toDto() }

fun hjemlerPerTema(): List<HjemlerPerTema> = hjemlerPerTema.map { HjemlerPerTema(it.tema.id, it.hjemler.toDto()) }
