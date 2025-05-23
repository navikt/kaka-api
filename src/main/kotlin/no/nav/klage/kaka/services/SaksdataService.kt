package no.nav.klage.kaka.services

import no.nav.klage.kaka.clients.azure.AzureGateway
import no.nav.klage.kaka.clients.egenansatt.EgenAnsattService
import no.nav.klage.kaka.clients.pdl.PdlFacade
import no.nav.klage.kaka.domain.KvalitetsvurderingReference
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.kodeverk.Role.*
import no.nav.klage.kaka.domain.kvalitetsvurdering.v1.KvalitetsvurderingV1
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kaka.domain.noKvalitetsvurderingNeeded
import no.nav.klage.kaka.exceptions.SaksdataFinalizedException
import no.nav.klage.kaka.exceptions.SaksdataNotFoundException
import no.nav.klage.kaka.exceptions.SectionedValidationErrorWithDetailsException
import no.nav.klage.kaka.exceptions.ValidationSection
import no.nav.klage.kaka.repositories.KvalitetsvurderingV1Repository
import no.nav.klage.kaka.repositories.KvalitetsvurderingV2Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getTeamLogger
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.Source
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.Utfall
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import no.nav.klage.kodeverk.ytelse.Ytelse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalTime
import java.util.*

@Service
@Transactional
class SaksdataService(
    private val saksdataRepository: SaksdataRepository,
    private val kvalitetsvurderingV1Repository: KvalitetsvurderingV1Repository,
    private val kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository,
    private val kvalitetsvurderingV1Service: KvalitetsvurderingV1Service,
    private val kvalitetsvurderingV2Service: KvalitetsvurderingV2Service,
    private val azureGateway: AzureGateway,
    private val tokenUtil: TokenUtil,
    private val rolleMapper: RolleMapper,
    private val pdlFacade: PdlFacade,
    private val egenAnsattService: EgenAnsattService,
    @Value("#{T(java.time.LocalDate).parse('\${KAKA_VERSION_2_DATE}')}")
    private val kakaVersion2Date: LocalDate,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val teamLogger = getTeamLogger()
    }

    fun getSaksdata(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        return getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
    }

    fun createSaksdata(innloggetSaksbehandler: String): Saksdata {
        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet

        return saksdataRepository.save(
            when (getKakaVersion()) {
                1 -> {
                    val kvalitetsvurderingV1 = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1())
                    Saksdata(
                        utfoerendeSaksbehandler = innloggetSaksbehandler,
                        tilknyttetEnhet = enhet.navn,
                        kvalitetsvurderingReference = KvalitetsvurderingReference(
                            id = kvalitetsvurderingV1.id,
                            version = 1,
                        ),
                    )
                }

                2 -> {
                    val kvalitetsvurderingV2 = kvalitetsvurderingV2Repository.save(KvalitetsvurderingV2())
                    Saksdata(
                        utfoerendeSaksbehandler = innloggetSaksbehandler,
                        tilknyttetEnhet = enhet.navn,
                        kvalitetsvurderingReference = KvalitetsvurderingReference(
                            id = kvalitetsvurderingV2.id,
                            version = 2,
                        ),
                    )
                }

                else -> error("Unknown kvalitetsvurdering version")
            }
        )
    }

    private fun getKakaVersion(): Int {
        val kvalitetsvurderingVersion = if (LocalDate.now() >= kakaVersion2Date) {
            2
        } else {
            1
        }
        return kvalitetsvurderingVersion
    }

    fun handleIncomingCompleteSaksdata(
        sakenGjelder: String,
        sakstype: Type,
        ytelse: Ytelse,
        mottattVedtaksinstans: LocalDate?,
        vedtaksinstansEnhet: String?,
        mottattKlageinstans: LocalDate,
        utfall: Utfall,
        hjemler: List<Registreringshjemmel>,
        utfoerendeSaksbehandler: String,
        tilknyttetEnhet: String,
        kvalitetsvurderingId: UUID,
        avsluttetAvSaksbehandler: LocalDateTime,
        source: Source,
        tilbakekreving: Boolean,
    ): Saksdata {
        val existingSaksdata = saksdataRepository.findOneByKvalitetsvurderingReferenceId(kvalitetsvurderingId)

        if (sakstype in listOf(
                Type.BEHANDLING_ETTER_TRYGDERETTEN_OPPHEVET,
                Type.OMGJOERINGSKRAV
            ) || utfall in noKvalitetsvurderingNeeded
        ) {
            kvalitetsvurderingV2Repository.save(
                KvalitetsvurderingV2(
                    id = kvalitetsvurderingId
                ),
            )
        } else {
            kvalitetsvurderingV2Service.cleanUpKvalitetsvurdering(kvalitetsvurderingId)
        }

        return if (existingSaksdata != null) {
            existingSaksdata.sakenGjelder = sakenGjelder
            existingSaksdata.sakstype = sakstype
            existingSaksdata.ytelse = ytelse
            existingSaksdata.mottattVedtaksinstans = mottattVedtaksinstans
            existingSaksdata.vedtaksinstansEnhet = vedtaksinstansEnhet
            existingSaksdata.mottattKlageinstans = mottattKlageinstans
            existingSaksdata.utfall = utfall
            existingSaksdata.registreringshjemler = hjemler.toSet()
            existingSaksdata.utfoerendeSaksbehandler = utfoerendeSaksbehandler
            existingSaksdata.tilknyttetEnhet = tilknyttetEnhet
            existingSaksdata.avsluttetAvSaksbehandler = avsluttetAvSaksbehandler
            existingSaksdata.source = source
            existingSaksdata.modified = now()
            existingSaksdata.tilbakekreving = tilbakekreving

            existingSaksdata
        } else {
            saksdataRepository.save(
                Saksdata(
                    sakenGjelder = sakenGjelder,
                    sakstype = sakstype,
                    ytelse = ytelse,
                    mottattKlageinstans = mottattKlageinstans,
                    vedtaksinstansEnhet = vedtaksinstansEnhet,
                    mottattVedtaksinstans = mottattVedtaksinstans,
                    utfall = utfall,
                    registreringshjemler = hjemler.toSet(),
                    avsluttetAvSaksbehandler = avsluttetAvSaksbehandler,
                    utfoerendeSaksbehandler = utfoerendeSaksbehandler,
                    tilknyttetEnhet = tilknyttetEnhet,
                    kvalitetsvurderingReference = KvalitetsvurderingReference(
                        id = kvalitetsvurderingId,
                        version = 2,
                    ),
                    source = source,
                    tilbakekreving = tilbakekreving,
                )
            )
        }
    }

    fun setSakenGjelder(saksdataId: UUID, sakenGjelder: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.sakenGjelder = sakenGjelder
        saksdata.modified = now()
        return saksdata
    }

    fun setSakstype(saksdataId: UUID, sakstype: Type, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.sakstype = sakstype
        saksdata.modified = now()
        return saksdata
    }

    fun setYtelse(saksdataId: UUID, ytelse: Ytelse?, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        if (saksdata.ytelse != ytelse) {
            setRegistreringshjemler(saksdataId, emptySet(), innloggetSaksbehandler)
        }
        saksdata.ytelse = ytelse
        saksdata.modified = now()
        return saksdata
    }

    fun setMottattVedtaksinstans(saksdataId: UUID, dato: LocalDate?, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.mottattVedtaksinstans = dato
        saksdata.modified = now()
        return saksdata
    }

    fun setVedtaksinstansEnhet(saksdataId: UUID, enhetsnummer: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.vedtaksinstansEnhet = enhetsnummer
        saksdata.modified = now()
        return saksdata
    }

    fun setMottattKlageinstans(saksdataId: UUID, dato: LocalDate?, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.mottattKlageinstans = dato
        saksdata.modified = now()
        return saksdata
    }

    fun setUtfall(saksdataId: UUID, utfall: Utfall, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.utfall = utfall
        saksdata.modified = now()
        return saksdata
    }

    fun setTilbakekreving(saksdataId: UUID, tilbakekreving: Boolean, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.tilbakekreving = tilbakekreving
        saksdata.modified = now()
        return saksdata
    }

    fun setRegistreringshjemler(
        saksdataId: UUID,
        registreringshjemler: Set<Registreringshjemmel>,
        innloggetSaksbehandler: String
    ): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.registreringshjemler = registreringshjemler.toMutableSet()
        saksdata.modified = now()
        return saksdata
    }

    fun setAvsluttetAvSaksbehandler(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)

        validate(saksdata)

        when (val version = saksdata.kvalitetsvurderingReference.version) {
            1 -> {
                if (saksdata.sakstype == Type.ANKE) {
                    saksdata.mottattVedtaksinstans = null
                    kvalitetsvurderingV1Service.removeFieldsUnusedInAnke(saksdata.kvalitetsvurderingReference.id)
                }
                if (saksdata.hasKvalitetsvurdering()) {
                    kvalitetsvurderingV1Service.cleanUpKvalitetsvurdering(saksdata.kvalitetsvurderingReference.id)
                } else {
                    kvalitetsvurderingV1Repository.save(
                        KvalitetsvurderingV1(
                            id = saksdata.kvalitetsvurderingReference.id
                        )
                    )
                }
            }

            2 -> {
                if (saksdata.sakstype == Type.ANKE) {
                    saksdata.mottattVedtaksinstans = null
                    kvalitetsvurderingV2Service.removeFieldsUnusedInAnke(saksdata.kvalitetsvurderingReference.id)
                }
                if (saksdata.hasKvalitetsvurdering()) {
                    kvalitetsvurderingV2Service.cleanUpKvalitetsvurdering(saksdata.kvalitetsvurderingReference.id)
                } else {
                    kvalitetsvurderingV2Repository.save(
                        KvalitetsvurderingV2(
                            id = saksdata.kvalitetsvurderingReference.id
                        )
                    )
                }
            }

            else -> error("Unknown version: $version")
        }

        saksdata.avsluttetAvSaksbehandler = now()
        saksdata.modified = now()
        return saksdata
    }

    private fun validate(saksdata: Saksdata) {
        val sectionList = saksdata.validateAndGetErrors()

        if (saksdata.utfall !in noKvalitetsvurderingNeeded) {
            val kvalitetsvurderingValidationErrors = when (saksdata.kvalitetsvurderingReference.version) {
                1 -> {
                    val kvalitetsvurderingV1 =
                        kvalitetsvurderingV1Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)
                    kvalitetsvurderingV1.getInvalidProperties(ytelse = saksdata.ytelse, type = saksdata.sakstype)
                }

                2 -> {
                    val kvalitetsvurderingV2 =
                        kvalitetsvurderingV2Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)
                    kvalitetsvurderingV2.getInvalidProperties(
                        ytelse = saksdata.ytelse,
                        type = saksdata.sakstype,
                    )
                }

                else -> error("unknown version: ${saksdata.kvalitetsvurderingReference.version}")
            }

            if (kvalitetsvurderingValidationErrors.isNotEmpty()) {
                sectionList.add(
                    ValidationSection(
                        section = "kvalitetsvurdering",
                        properties = kvalitetsvurderingValidationErrors
                    )
                )
            }
        }

        if (sectionList.isNotEmpty()) {
            throw SectionedValidationErrorWithDetailsException(
                title = "Validation error",
                sections = sectionList
            )
        }
    }

    fun reopenSaksdata(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(
            saksdataId = saksdataId,
            innloggetSaksbehandler = innloggetSaksbehandler,
            isReopen = true
        )

        when (getKakaVersion()) {
            1 -> {
                if (saksdata.kvalitetsvurderingReference.version != 1) {
                    kvalitetsvurderingV2Repository.deleteById(saksdata.kvalitetsvurderingReference.id)
                    val kvalitetsvurderingV1 = kvalitetsvurderingV1Repository.save(KvalitetsvurderingV1())
                    saksdata.kvalitetsvurderingReference = KvalitetsvurderingReference(
                        id = kvalitetsvurderingV1.id,
                        version = 1
                    )
                }
            }

            2 -> {
                if (saksdata.kvalitetsvurderingReference.version != 2) {
                    kvalitetsvurderingV1Repository.deleteById(saksdata.kvalitetsvurderingReference.id)
                    val kvalitetsvurderingV2 = kvalitetsvurderingV2Repository.save(KvalitetsvurderingV2())
                    saksdata.kvalitetsvurderingReference = KvalitetsvurderingReference(
                        id = kvalitetsvurderingV2.id,
                        version = 2
                    )
                }
            }

            else -> error("Invalid version")
        }
        saksdata.avsluttetAvSaksbehandler = null
        saksdata.modified = now()

        return saksdata
    }

    private fun getSaksdataAndVerifyAccess(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = saksdataRepository.findById(saksdataId)
        if (saksdata.isEmpty) {
            throw SaksdataNotFoundException("Could not find saksdata with id $saksdataId")
        }
        return saksdata.get().also { s ->
            s.verifyReadAccess(
                innloggetIdent = innloggetSaksbehandler,
                roller = rolleMapper.toRoles(tokenUtil.getGroups()),
                ansattEnhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet.navn
            )
        }
    }

    private fun getSaksdataAndVerifyAccessForEdit(
        saksdataId: UUID,
        innloggetSaksbehandler: String,
        isReopen: Boolean = false
    ): Saksdata {
        val saksdata = saksdataRepository.findById(saksdataId)
        if (saksdata.isEmpty) {
            throw SaksdataNotFoundException("Could not find saksdata with id $saksdataId")
        }
        return saksdata.get().also {
            it.verifyWriteAccess(innloggetSaksbehandler)
            if (!isReopen && it.avsluttetAvSaksbehandler != null) throw SaksdataFinalizedException("Saksdataen er allerede fullført")
        }
    }

    fun search(saksbehandlerIdent: String, fullfoert: Boolean, daysSince: Int?): List<Saksdata> {
        return if (fullfoert) {
            val dateFrom = LocalDate.now().atStartOfDay().minusDays(daysSince?.toLong() ?: 7)
            saksdataRepository.findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerGreaterThanEqualOrderByModified(
                saksbehandlerIdent,
                dateFrom
            )
        } else {
            saksdataRepository.findByUtfoerendeSaksbehandlerAndAvsluttetAvSaksbehandlerIsNullOrderByCreated(
                saksbehandlerIdent
            )
        }
    }

    fun searchAsVedtaksinstanslederV1(
        saksbehandlerIdent: String,
        enhet: Enhet,
        fromDate: LocalDate,
        toDate: LocalDate,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<Saksdata> {
        val roller = rolleMapper.toRoles(tokenUtil.getGroups())

        val kanBehandleStrengtFortrolig = STRENGT_FORTROLIG in roller
        val kanBehandleFortrolig = FORTROLIG in roller
        val kanBehandleEgenAnsatt = EGEN_ANSATT in roller

        return saksdataRepository.findForVedtaksinstanslederWithEnhetV1(
            vedtaksinstansEnhet = enhet.navn,
            fromDateTime = fromDate.atStartOfDay(),
            toDateTime = toDate.atTime(LocalTime.MAX),
            mangelfullt = mangelfullt,
            kommentarer = kommentarer,
        ).filter {
            verifiserTilgangTilPersonForSaksbehandler(
                fnr = it.saksdata.sakenGjelder ?: throw RuntimeException("missing fnr"),
                ident = saksbehandlerIdent,
                kanBehandleStrengtFortrolig = kanBehandleStrengtFortrolig,
                kanBehandleFortrolig = kanBehandleFortrolig,
                kanBehandleEgenAnsatt = kanBehandleEgenAnsatt,
            )
        }.map { it.saksdata }
    }

    fun searchAsVedtaksinstanslederV2(
        saksbehandlerIdent: String,
        enhet: Enhet,
        fromDate: LocalDate,
        toDate: LocalDate,
        mangelfullt: List<String>,
    ): List<Saksdata> {
        val roller = rolleMapper.toRoles(tokenUtil.getGroups())

        val kanBehandleStrengtFortrolig = STRENGT_FORTROLIG in roller
        val kanBehandleFortrolig = FORTROLIG in roller
        val kanBehandleEgenAnsatt = EGEN_ANSATT in roller

        return saksdataRepository.findForVedtaksinstanslederWithEnhetV2(
            vedtaksinstansEnhet = enhet.navn,
            fromDateTime = fromDate.atStartOfDay(),
            toDateTime = toDate.atTime(LocalTime.MAX),
            mangelfullt = mangelfullt,
        ).filter {
            verifiserTilgangTilPersonForSaksbehandler(
                fnr = it.saksdata.sakenGjelder ?: throw RuntimeException("missing fnr"),
                ident = saksbehandlerIdent,
                kanBehandleStrengtFortrolig = kanBehandleStrengtFortrolig,
                kanBehandleFortrolig = kanBehandleFortrolig,
                kanBehandleEgenAnsatt = kanBehandleEgenAnsatt,
            )
        }.map { it.saksdata }
    }

    fun deleteSaksdata(saksdataId: UUID, innloggetSaksbehandler: String) {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)

        when (saksdata.kvalitetsvurderingReference.version) {
            1 -> {
                kvalitetsvurderingV1Repository.deleteById(saksdata.kvalitetsvurderingReference.id)
            }

            2 -> {
                kvalitetsvurderingV2Repository.deleteById(saksdata.kvalitetsvurderingReference.id)
            }

            else -> {
                error("Unknown version ${saksdata.kvalitetsvurderingReference.version}")
            }
        }

        saksdataRepository.deleteById(saksdataId)
    }

    private fun verifiserTilgangTilPersonForSaksbehandler(
        fnr: String,
        ident: String,
        kanBehandleStrengtFortrolig: Boolean,
        kanBehandleFortrolig: Boolean,
        kanBehandleEgenAnsatt: Boolean
    ): Boolean {
        try {
            //if foretak, no need to check access
            if (fnr.length == 9) {
                return true
            }
            val personInfo = pdlFacade.getPersonInfo(fnr)
            val harBeskyttelsesbehovFortrolig = personInfo.harBeskyttelsesbehovFortrolig()
            val harBeskyttelsesbehovStrengtFortrolig = personInfo.harBeskyttelsesbehovStrengtFortrolig()
            val erEgenAnsatt = egenAnsattService.erEgenAnsatt(fnr)

            if (harBeskyttelsesbehovStrengtFortrolig) {
                logger.debug("erStrengtFortrolig. See more details in team-logs.")
                //Merk at vi ikke sjekker egenAnsatt her, strengt fortrolig trumfer det
                if (kanBehandleStrengtFortrolig) {
                    teamLogger.debug("Access granted to strengt fortrolig for $ident")
                } else {
                    teamLogger.debug("Access denied to strengt fortrolig for $ident")
                    return false
                }
            }
            if (harBeskyttelsesbehovFortrolig) {
                logger.debug("erFortrolig. See more details in team-logs.")
                //Merk at vi ikke sjekker egenAnsatt her, fortrolig trumfer det
                if (kanBehandleFortrolig) {
                    teamLogger.debug("Access granted to fortrolig for $ident")
                } else {
                    teamLogger.debug("Access denied to fortrolig for $ident")
                    return false
                }
            }
            if (erEgenAnsatt && !(harBeskyttelsesbehovFortrolig || harBeskyttelsesbehovStrengtFortrolig)) {
                logger.debug("erEgenAnsatt. See more details in team-logs.")
                //Er kun egenAnsatt, har ikke et beskyttelsesbehov i tillegg
                if (kanBehandleEgenAnsatt) {
                    teamLogger.debug("Access granted to egen ansatt for $ident")
                } else {
                    teamLogger.debug("Access denied to egen ansatt for $ident")
                    return false
                }
            }
            return true
        } catch (e: Exception) {
            logger.warn("Could not verify access to person. See team-logs for more details.")
            teamLogger.warn("Could not verify access to person", e)
            return false
        }
    }
}