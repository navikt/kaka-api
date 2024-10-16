package no.nav.klage.kaka.repositories

import java.time.LocalDate
import java.time.LocalDateTime

interface SaksdataRepositoryCustom {

    fun findForVedtaksinstanslederV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findForVedtaksinstanslederWithEnhetV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
        vedtaksinstansEnhet: String,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findForVedtaksinstanslederV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV2>

    fun findForVedtaksinstanslederWithEnhetV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        vedtaksinstansEnhet: String,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV2>

    fun findByAvsluttetAvSaksbehandlerBetweenV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findByAvsluttetAvSaksbehandlerBetweenV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV2>

    fun findByQueryParamsV1(
        fromDate: LocalDate,
        toDate: LocalDate,
        tilbakekreving: String,
        klageenheter: List<String>?,
        vedtaksinstansgrupper: List<Int>?,
        enheter: List<String>?,
        types: List<String>?,
        ytelser: List<String>?,
        utfall: List<String>?,
        hjemler: List<String>?,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findByQueryParamsV2(
        fromDate: LocalDate,
        toDate: LocalDate,
        tilbakekreving: String,
        klageenheter: List<String>?,
        vedtaksinstansgrupper: List<Int>?,
        enheter: List<String>?,
        types: List<String>?,
        ytelser: List<String>?,
        utfall: List<String>?,
        hjemler: List<String>?,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV2>

    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV2(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV2>

}