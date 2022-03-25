package no.nav.klage.kaka.domain.kodeverk

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class RadioValg(override val id: String, override val navn: String) : RadioValgKode {
    BRA("0", "Bra"),
    MANGELFULLT("1", "Mangelfullt");

    override fun toString(): String {
        return "RadioValg(id=$id, " +
                "navn=$navn)"
    }

    companion object {
        fun of(id: String): RadioValg {
            return values().firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("No RadioValg with $id exists")
        }

        fun fromNavn(navn: String): RadioValg {
            return values().firstOrNull { it.navn == navn }
                ?: throw IllegalArgumentException("No RadioValg with $navn exists")
        }
    }
}

@Converter
class RadioValgConverter : AttributeConverter<RadioValg, String?> {

    override fun convertToDatabaseColumn(entity: RadioValg?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): RadioValg? =
        id?.let { RadioValg.of(it) }
}