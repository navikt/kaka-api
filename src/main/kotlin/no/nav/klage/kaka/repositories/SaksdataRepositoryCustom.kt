package no.nav.klage.kaka.repositories

import java.time.LocalDateTime

interface SaksdataRepositoryCustom {

    fun findForVedtaksinstanslederV1(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<SaksdataRepositoryCustomImpl.ResultV1>

    fun findForVedtaksinstanslederV2(
        vedtaksinstansEnhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<SaksdataRepositoryCustomImpl.ResultV2>

    fun findByAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): List<SaksdataRepositoryCustomImpl.ResultV1>

    fun findByAvsluttetAvSaksbehandlerBetweenOrderByCreatedV2(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime
    ): List<SaksdataRepositoryCustomImpl.ResultV2>

    fun findByAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreatedV1(
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandler: String,
    ): List<SaksdataRepositoryCustomImpl.ResultV1>

    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
    ): List<SaksdataRepositoryCustomImpl.ResultV1>

    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreatedV1(
        enhet: String,
        fromDateTime: LocalDateTime,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): List<SaksdataRepositoryCustomImpl.ResultV1>

    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreatedV1(
        enhet: String,
        toDateTime: LocalDateTime
    ): List<SaksdataRepositoryCustomImpl.ResultV1>

    fun findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerInOrderByCreatedV1(
        enhet: String,
        toDateTime: LocalDateTime,
        saksbehandlerIdentList: List<String>
    ): List<SaksdataRepositoryCustomImpl.ResultV1>

    fun findByAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreatedV1(
        toDateTime: LocalDateTime
    ): List<SaksdataRepositoryCustomImpl.ResultV1>

    fun findByAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerOrderByCreatedV1(
        toDateTime: LocalDateTime,
        saksbehandler: String,
    ): List<SaksdataRepositoryCustomImpl.ResultV1>
}