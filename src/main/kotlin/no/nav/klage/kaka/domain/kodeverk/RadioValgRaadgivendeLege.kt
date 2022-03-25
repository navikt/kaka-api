package no.nav.klage.kaka.domain.kodeverk

import javax.persistence.AttributeConverter
import javax.persistence.Converter

enum class RadioValgRaadgivendeLege(override val id: String, override val navn: String) : RadioValgKode {
    IKKE_AKTUELT("0", "Ikke aktuelt"),
    BRA("1", "Bra"),
    MANGELFULLT("2", "Mangelfullt");

    override fun toString(): String {
        return "RadioValgRaadgivendeLege(id=$id, " +
                "navn=$navn)"
    }

    companion object {
        fun of(id: String): RadioValgRaadgivendeLege {
            return values().firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("No RadioValgRaadgivendeLege with $id exists")
        }

        fun fromNavn(navn: String): RadioValgRaadgivendeLege {
            return values().firstOrNull { it.navn == navn }
                ?: throw IllegalArgumentException("No RadioValgRaadgivendeLege with $navn exists")
        }
    }
}

@Converter
class RadioValgRaadgivendeLegeConverter : AttributeConverter<RadioValgRaadgivendeLege, String?> {

    override fun convertToDatabaseColumn(entity: RadioValgRaadgivendeLege?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): RadioValgRaadgivendeLege? =
        id?.let { RadioValgRaadgivendeLege.of(it) }
}

