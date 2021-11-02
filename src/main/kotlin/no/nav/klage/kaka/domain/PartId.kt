package no.nav.klage.kaka.domain

import no.nav.klage.kaka.domain.kodeverk.PartIdType
import no.nav.klage.kaka.domain.kodeverk.PartIdTypeConverter
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Embeddable


@Embeddable
data class PartId(
    @Column(name = "type")
    @Convert(converter = PartIdTypeConverter::class)
    val type: PartIdType,
    @Column(name = "value")
    val value: String,
)
