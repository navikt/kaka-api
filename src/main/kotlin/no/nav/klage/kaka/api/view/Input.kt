package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1.*
import java.time.LocalDate

data class StringInput(
    val value: String
)

data class DateInput(
    val value: LocalDate
)

data class NullableDateInput(
    val value: LocalDate?
)

data class RegistreringshjemlerInput(
    val value: Set<String>
)

data class BooleanInput(
    val value: Boolean
)

data class RadioValgInput(
    val value: RadioValg
)

data class RadioValgRaadgivendeLegeInput(
    val value: RadioValgRaadgivendeLege
)