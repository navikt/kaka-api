package no.nav.klage.kaka.domain

import no.nav.klage.kodeverk.Kode
import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class Source(override val id: String, override val navn: String, override val beskrivelse: String) : Kode {
    KAKA("1", "KAKA", "KAKA"),
    KABAL("2", "KABAL", "KABAL");

    companion object {
        fun of(id: String): Source {
            return values().firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("No Source with $id exists")
        }
    }
}

@Converter
class SourceConverter : AttributeConverter<Source, String?> {

    override fun convertToDatabaseColumn(entity: Source?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): Source? =
        id?.let { Source.of(it) }
}