package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import java.time.LocalDate

data class StringInput(
    val value: String
)

data class DateInput(
    val value: LocalDate
)

data class HjemlerInput(
    val value: Set<String>?
)

data class BooleanInput(
    val value: Boolean
)

data class RadioValgInput(
    val value: Kvalitetsvurdering.RadioValg
)

data class RadioValgRaadgivendeLegeInput(
    val value: Kvalitetsvurdering.RadioValgRaadgivendeLege
)