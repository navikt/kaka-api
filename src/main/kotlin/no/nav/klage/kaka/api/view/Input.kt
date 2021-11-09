package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.kodeverk.Sakstype
import no.nav.klage.kaka.domain.kodeverk.Tema
import no.nav.klage.kaka.domain.kodeverk.Utfall
import java.time.LocalDate

data class KlagerInput(
    val value: String
)

data class SakstypeInput(
    val value: Sakstype
)

data class TemaInput(
    val value: Tema
)

data class DatoInput(
    val value: LocalDate
)

data class VedtaksinstansEnhetInput(
    val value: String
)

data class UtfallInput(
    val value: Utfall
)

data class HjemlerInput(
    val value: Set<String>?
)

data class BooleanInput(
    val value: Boolean
)

data class TextInput(
    val value: String
)

data class RadioValgInput(
    val value: Kvalitetsvurdering.RadioValg
)

data class RadioValgRaadgivendeLegeInput(
    val value: Kvalitetsvurdering.RadioValgRaadgivendeLege
)