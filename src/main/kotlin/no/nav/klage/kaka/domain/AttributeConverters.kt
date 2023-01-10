package no.nav.klage.kaka.domain

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel

@Converter
class PartIdTypeConverter : AttributeConverter<PartIdType, String?> {

    override fun convertToDatabaseColumn(entity: PartIdType?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): PartIdType? =
        id?.let { PartIdType.of(it) }
}

@Converter
class TypeConverter : AttributeConverter<Type, String?> {

    override fun convertToDatabaseColumn(entity: Type?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): Type? =
        id?.let { Type.of(it) }
}

@Converter
class UtfallConverter : AttributeConverter<Utfall, String?> {

    override fun convertToDatabaseColumn(entity: Utfall?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): Utfall? =
        id?.let { Utfall.of(it) }
}

@Converter
class YtelseConverter : AttributeConverter<Ytelse, String?> {

    override fun convertToDatabaseColumn(entity: Ytelse?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): Ytelse? =
        id?.let { Ytelse.of(it) }
}

@Converter
class RegistreringshjemmelConverter : AttributeConverter<Registreringshjemmel, String?> {

    override fun convertToDatabaseColumn(entity: Registreringshjemmel?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): Registreringshjemmel? =
        id?.let { Registreringshjemmel.of(it) }
}

@Converter
class SourceConverter : AttributeConverter<Source, String?> {

    override fun convertToDatabaseColumn(entity: Source?): String? =
        entity?.id

    override fun convertToEntityAttribute(id: String?): Source? =
        id?.let { Source.of(it) }
}