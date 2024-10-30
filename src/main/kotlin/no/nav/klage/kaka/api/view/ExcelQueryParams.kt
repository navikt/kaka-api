package no.nav.klage.kaka.api.view

import java.time.LocalDate

data class ExcelQueryParams(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val tilbakekreving: String,
    val klageenheter: List<String>?,
    val vedtaksinstansgrupper: List<Int>?,
    val enheter: List<String>?,
    val types: List<String>?,
    val ytelser: List<String>?,
    val utfall: List<String>?,
    val hjemler: List<String>?,
)