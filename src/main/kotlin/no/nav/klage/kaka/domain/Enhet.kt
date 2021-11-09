package no.nav.klage.kaka.domain

import no.nav.klage.kaka.domain.kodeverk.Kode

/**
 * Hardcoded for now. Maybe get from Axsys or similar later.
 */
enum class Enhet(override val id: String, override val navn: String, override val beskrivelse: String) : Kode {

    NAV_MOSS("0104", "NAV Moss", "NAV Moss - 0104"),
    NAV_XXXX("xxxx", "NAV xxxx", "NAV xxxx - xxxx"),
    NAV_YYYY("yyyy", "NAV yyyy", "NAV yyyy - yyyy"),

}