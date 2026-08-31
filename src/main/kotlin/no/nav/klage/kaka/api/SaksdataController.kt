package no.nav.klage.kaka.api

import io.swagger.v3.oas.annotations.tags.Tag
import no.nav.klage.kaka.api.view.BooleanInput
import no.nav.klage.kaka.api.view.NullableDateInput
import no.nav.klage.kaka.api.view.RegistreringshjemlerInput
import no.nav.klage.kaka.api.view.SaksdataView
import no.nav.klage.kaka.api.view.StringInput
import no.nav.klage.kaka.api.view.toSaksdataView
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
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.ytelse.Ytelse
import no.nav.security.token.support.core.api.ProtectedWithClaims
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
        @PathVariable("id") saksdataId: UUID,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::getSaksdata.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService.getSaksdata(saksdataId = saksdataId, innloggetSaksbehandler = innloggetSaksbehandler).toSaksdataView()
    }

    @DeleteMapping("/{id}")
    fun deleteSaksdata(
        @PathVariable("id") saksdataId: UUID,
    ) {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::deleteSaksdata.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        saksdataService.deleteSaksdata(saksdataId = saksdataId, innloggetSaksbehandler = innloggetSaksbehandler)
    }

    @PostMapping
    fun createSaksdata(): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::createSaksdata.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = UUID.randomUUID(),
            logger = logger,
        )

        val roles = rolleMapper.toRoles(tokenUtil.getGroups())
        if (KAKA_KVALITETSVURDERING !in roles) {
            throw MissingTilgangException("User does not have access to create saksdata")
        }

        return saksdataService
            .createSaksdata(
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
            methodName = ::reopenSaksdata.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = UUID.randomUUID(),
            logger = logger,
        )

        return saksdataService.reopenSaksdata(saksdataId = saksdataId, innloggetSaksbehandler = innloggetSaksbehandler).toSaksdataView()
    }

    @PutMapping("/{id}/sakengjelder")
    fun setSakenGjelder(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setSakenGjelder.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setSakenGjelder(
                saksdataId = saksdataId,
                sakenGjelder = input.value,
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PutMapping("/{id}/sakstype")
    fun setSakstype(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setSakstype.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setSakstype(saksdataId = saksdataId, sakstype = Type.of(input.value), innloggetSaksbehandler = innloggetSaksbehandler)
            .toSaksdataView()
    }

    @PutMapping("/{id}/sakstypeid")
    fun setSakstypeId(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setSakstypeId.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setSakstype(saksdataId = saksdataId, sakstype = Type.of(input.value), innloggetSaksbehandler = innloggetSaksbehandler)
            .toSaksdataView()
    }

    @PutMapping("/{id}/ytelse")
    fun setYtelse(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setYtelse.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setYtelse(
                saksdataId = saksdataId,
                ytelse = Ytelse.of(input.value),
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PutMapping("/{id}/ytelseid")
    fun setYtelseId(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setYtelseId.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setYtelse(
                saksdataId = saksdataId,
                ytelse = Ytelse.of(input.value),
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PutMapping("/{id}/mottattvedtaksinstans")
    fun setMottattVedtaksinstans(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: NullableDateInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setMottattVedtaksinstans.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setMottattVedtaksinstans(saksdataId = saksdataId, dato = input.value, innloggetSaksbehandler = innloggetSaksbehandler)
            .toSaksdataView()
    }

    @PutMapping("/{id}/vedtaksinstansenhet")
    fun setVedtaksinstansEnhet(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setVedtaksinstansEnhet.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        validateEnhetsnummer(input.value)

        return saksdataService
            .setVedtaksinstansEnhet(
                saksdataId = saksdataId,
                enhetsnummer = input.value,
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PutMapping("/{id}/mottattklageinstans")
    fun setMottattKlageinstans(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: NullableDateInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setMottattKlageinstans.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setMottattKlageinstans(
                saksdataId = saksdataId,
                dato = input.value,
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PutMapping("/{id}/utfall")
    fun setUtfall(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setUtfall.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setUtfall(
                saksdataId = saksdataId,
                utfall = Utfall.of(input.value),
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PutMapping("/{id}/utfallid")
    fun setUtfallId(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: StringInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setUtfallId.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setUtfall(
                saksdataId = saksdataId,
                utfall = Utfall.of(input.value),
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PutMapping("/{id}/tilbakekreving")
    fun setTilbakekreving(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: BooleanInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setTilbakekreving.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setTilbakekreving(
                saksdataId = saksdataId,
                tilbakekreving = input.value,
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PutMapping("/{id}/hjemmelidlist")
    fun setHjemmelIdList(
        @PathVariable("id") saksdataId: UUID,
        @RequestBody input: RegistreringshjemlerInput,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::setHjemmelIdList.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setRegistreringshjemler(
                saksdataId = saksdataId,
                registreringshjemler = input.value.map { Registreringshjemmel.of(it) }.toSet(),
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }

    @PostMapping("/{id}/fullfoer")
    fun fullfoerSaksdata(
        @PathVariable("id") saksdataId: UUID,
    ): SaksdataView {
        val innloggetSaksbehandler = tokenUtil.getIdent()
        logSaksdataMethodDetails(
            methodName = ::fullfoerSaksdata.name,
            innloggetIdent = innloggetSaksbehandler,
            saksdataId = saksdataId,
            logger = logger,
        )

        return saksdataService
            .setAvsluttetAvSaksbehandler(
                saksdataId = saksdataId,
                innloggetSaksbehandler = innloggetSaksbehandler,
            ).toSaksdataView()
    }
}
