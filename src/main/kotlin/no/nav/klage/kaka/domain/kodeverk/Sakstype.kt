package no.nav.klage.kaka.domain.kodeverk

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class Sakstype(override val id: String, override val navn: String, override val beskrivelse: String) : Kode  {
    KLAGE("1", "Klage", "Klagesak"),
    ANKE("2", "Anke", "Ankesak");

    override fun toString(): String {
        return "Tema(id=$id, " +
                "navn=$navn)"
    }

    companion object {
        fun of(id: String): Sakstype {
            return values().firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("No Sakstype with $id exists")
        }

        fun fromNavn(navn: String?): Sakstype {
            return values().firstOrNull { it.navn == navn }
                ?: throw IllegalArgumentException("No Sakstype with $navn exists")
        }
    }
}

@Converter
class SakstypeConverter : AttributeConverter<Sakstype, String?> {

    override fun convertToDatabaseColumn(entity: Sakstype?): String? =
        entity?.let { it.id }

    override fun convertToEntityAttribute(id: String?): Sakstype? =
        id?.let { Sakstype.of(it) }
}