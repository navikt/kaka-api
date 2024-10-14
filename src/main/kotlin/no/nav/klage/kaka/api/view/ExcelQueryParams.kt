package no.nav.klage.kaka.api.view

import java.time.LocalDate

//?version=2&fromDate=2024-10-01&toDate=2024-10-08&tilbakekreving=only&klageenheter=4295,4250&vedtaksinstansgrupper=0,8&enheter=4704,4702&types=2,1&ytelser=41,42&utfall=4,6&hjemler=308,FTRL_2
data class ExcelQueryParams(
    val version: Int,
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