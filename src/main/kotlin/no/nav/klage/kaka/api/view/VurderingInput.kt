package no.nav.klage.kaka.api.view

import no.nav.klage.kaka.domain.Kvalitetsvurdering
import no.nav.klage.kaka.domain.kodeverk.Sakstype
import no.nav.klage.kaka.domain.kodeverk.Tema
import no.nav.klage.kaka.domain.kodeverk.Utfall
import java.time.LocalDate

data class KlagerInput(
    val klager: String
)

data class SakstypeInput(
    val sakstype: Sakstype
)

data class TemaInput(
    val tema: Tema
)

data class DatoInput(
    val dato: LocalDate
)

data class VedtaksinstansEnhetInput(
    val enhet: String
)

data class UtfallInput(
    val utfall: Utfall
)

data class HjemlerInput(
    val hjemler: Set<String>?
)

data class BooleanInput(
    val selected: Boolean
)

data class TextInput(
    val text: String
)

data class RadioValgInput(
    val selection: Kvalitetsvurdering.RadioValg
)

data class RadioValgRaadgivendeLegeInput(
    val selection: Kvalitetsvurdering.RadioValgRaadgivendeLege
)