package no.nav.klage.kaka.api.view

import java.time.LocalDate

data class StringInput(
    val value: String
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