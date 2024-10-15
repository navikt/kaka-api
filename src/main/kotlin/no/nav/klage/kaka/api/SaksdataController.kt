package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.*
import no.nav.klage.kaka.config.SecurityConfig
import no.nav.klage.kaka.domain.kodeverk.Role.KAKA_KVALITETSVURDERING
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.services.SaksdataService
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.logSaksdataMethodDetails
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Utfall
import no.nav.klage.kodeverk.Ytelse
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@Tag(name = "kaka-api:saksdata")
@ProtectedWithClaims(issuer = SecurityConfig.ISSUER_AAD)
@RequestMapping("/saksdata")
class SaksdataController(
    private val saksdataService: SaksdataService,
    private val tokenUtil: TokenUtil,
    private val rolleMapper: RolleMapper,
) {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    @GetMapping("/{id}")
    fun getSaksdata(
        @PathVariable("id") saksdataId: UUID
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::getSaksdata.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.getSaksdata(saksdataId, innloggetSaksbehandler).toSaksdataView()
    }

    @DeleteMapping("/{id}")
    fun deleteSaksdata(
        @PathVariable("id") saksdataId: UUID
    ) {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::deleteSaksdata.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        saksdataService.deleteSaksdata(saksdataId, innloggetSaksbehandler)
    }

    @PostMapping
    fun createSaksdata(
        @RequestBody input: SaksdataInput? = null
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::createSaksdata.name,
            innloggetSaksbehandler,
            UUID.randomUUID(),
            logger
        )

        val roles = rolleMapper.toRoles(tokenUtil.getGroups())
        if (KAKA_KVALITETSVURDERING !in roles) {
            throw MissingTilgangException("User does not have access to create saksdata")
        }

        return saksdataService.createSaksdata(
            innloggetSaksbehandler = innloggetSaksbehandler,
        ).toSaksdataView()
    }

    private fun validateEnhetsnummer(enhetsnummer: String?) {
        if (!Enhet.values().any { it.navn == enhetsnummer }) {
            throw RuntimeException("Not valid enhetsnummer")
        }
    }

    @PostMapping("/{id}/reopen")
    fun reopenSaksdata(
        @PathVariable("id") saksdataId: UUID,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::reopenSaksdata.name,
            innloggetSaksbehandler,
            UUID.randomUUID(),
            logger
        )

        return saksdataService.reopenSaksdata(saksdataId, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/sakengjelder")
    fun setSakenGjelder(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setSakenGjelder.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setSakenGjelder(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/sakstype")
    fun setSakstype(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setSakstype.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setSakstype(saksdataId, Type.of(input.value), innloggetSaksbehandler)
            .toSaksdataView()
    }

    @PutMapping("/{id}/sakstypeid")
    fun setSakstypeId(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setSakstypeId.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setSakstype(saksdataId, Type.of(input.value), innloggetSaksbehandler)
            .toSaksdataView()
    }

    @PutMapping("/{id}/ytelse")
    fun setYtelse(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setYtelse.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setYtelse(saksdataId, Ytelse.of(input.value), innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/ytelseid")
    fun setYtelseId(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setYtelseId.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setYtelse(saksdataId, Ytelse.of(input.value), innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/mottattvedtaksinstans")
    fun setMottattVedtaksinstans(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: NullableDateInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setMottattVedtaksinstans.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setMottattVedtaksinstans(saksdataId, input.value, innloggetSaksbehandler)
            .toSaksdataView()
    }

    @PutMapping("/{id}/vedtaksinstansenhet")
    fun setVedtaksinstansEnhet(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setVedtaksinstansEnhet.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        validateEnhetsnummer(input.value)

        return saksdataService.setVedtaksinstansEnhet(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/mottattklageinstans")
    fun setMottattKlageinstans(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: NullableDateInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setMottattKlageinstans.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setMottattKlageinstans(saksdataId, input.value, innloggetSaksbehandler).toSaksdataView()
    }


    @PutMapping("/{id}/utfall")
    fun setUtfall(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setUtfall.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setUtfall(saksdataId, Utfall.of(input.value), innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/utfallid")
    fun setUtfallId(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setUtfallId.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setUtfall(saksdataId, Utfall.of(input.value), innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/tilbakekreving")
    fun setTilbakekreving(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: BooleanInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setTilbakekreving.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setTilbakekreving(
            saksdataId = saksdataId,
            tilbakekreving = input.value,
            innloggetSaksbehandler = innloggetSaksbehandler
        ).toSaksdataView()
    }

    @PutMapping("/{id}/hjemmelidlist")
    fun setHjemmelIdList(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: RegistreringshjemlerInput
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::setHjemmelIdList.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setRegistreringshjemler(
            saksdataId,
            input.value.map { Registreringshjemmel.of(it) }.toSet(),
            innloggetSaksbehandler
        ).toSaksdataView()
    }

    @PostMapping("/{id}/fullfoer")
    fun fullfoerSaksdata(
        @PathVariable("id") saksdataId: UUID
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            ::fullfoerSaksdata.name,
            innloggetSaksbehandler,
            saksdataId,
            logger
        )

        return saksdataService.setAvsluttetAvSaksbehandler(saksdataId, innloggetSaksbehandler).toSaksdataView()
    }
}