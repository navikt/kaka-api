package no.nav.klage.kaka.repositories

import java.time.LocalDateTime

interface SaksdataRepositoryCustom {

    fun findForVedtaksinstanslederV1(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findForVedtaksinstanslederV2(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<SaksdataRepositoryCustomImpl.QueryResultV2>

    fun findByAvsluttetAvSaksbehandlerBetweenV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findByAvsluttetAvSaksbehandlerBetweenV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV2>

    fun findByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreatedV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandler: String,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreatedV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandler: String,
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

    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreatedV1(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>

    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreatedV2(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV2>

    fun findByAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerOrderByCreatedV1(
        toDateTime: LocalDateTime,
        saksbehandler: String,
    ): Set<SaksdataRepositoryCustomImpl.QueryResultV1>
}