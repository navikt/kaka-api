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
import no.nav.klage.kaka.repositories.KvalitetsvurderingV1Repository
import no.nav.klage.kaka.repositories.KvalitetsvurderingV2Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.util.RolleMapper
import no.nav.klage.kaka.util.TokenUtil
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kaka.util.getSecureLogger
import no.nav.klage.kodeverk.*
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

@Service
@Transactional
class SaksdataService(
    private val saksdataRepository: SaksdataRepository,
    private val kvalitetsvurderingV1Repository: KvalitetsvurderingV1Repository,
    private val kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository,
    private val kvalitetsvurderingService: KvalitetsvurderingService,
    private val azureGateway: AzureGateway,
    private val tokenUtil: TokenUtil,
    private val rolleMapper: RolleMapper,
    private val pdlFacade: PdlFacade,
    private val egenAnsattService: EgenAnsattService,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
        private val secureLogger = getSecureLogger()
    }


    fun getSaksdata(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        return getSaksdataAndVerifyAccess(saksdataId, innloggetSaksbehandler)
    }

    fun createSaksdata(innloggetSaksbehandler: String, kvalitsvurderingVersion: Int? = 1): Saksdata {
        val enhet = azureGateway.getDataOmInnloggetSaksbehandler().enhet

        return saksdataRepository.save(
            when (kvalitsvurderingVersion) {
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

    fun handleIncomingCompleteSaksdata(
        sakenGjelder: String,
        sakstype: Type,
        ytelse: Ytelse,
        mottattVedtaksinstans: LocalDate?,
        vedtaksinstansEnhet: String,
        mottattKlageinstans: LocalDate,
        utfall: Utfall,
        hjemler: List<Registreringshjemmel>,
        utfoerendeSaksbehandler: String,
        tilknyttetEnhet: String,
        kvalitetsvurderingId: UUID,
        avsluttetAvSaksbehandler: LocalDateTime,
        source: Source
    ): Saksdata {
        val existingSaksdata = saksdataRepository.findOneByKvalitetsvurderingReferenceId(kvalitetsvurderingId)

        if (utfall !in noKvalitetsvurderingNeeded) {
            kvalitetsvurderingService.cleanUpKvalitetsvurdering(kvalitetsvurderingId)
        } else {
            kvalitetsvurderingV1Repository.save(
                KvalitetsvurderingV1(
                    id = kvalitetsvurderingId
                ),
            )
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
            existingSaksdata.modified = LocalDateTime.now()

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
                    //TODO
                    kvalitetsvurderingReference = KvalitetsvurderingReference(
                        id = kvalitetsvurderingV1Repository.getReferenceById(kvalitetsvurderingId).id,
                        version = 1,
                    ),
                    source = source
                )
            )
        }
    }

    fun setSakenGjelder(saksdataId: UUID, sakenGjelder: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.sakenGjelder = sakenGjelder
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setSakstype(saksdataId: UUID, sakstype: Type, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.sakstype = sakstype
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setYtelse(saksdataId: UUID, ytelse: Ytelse?, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        if (saksdata.ytelse != ytelse) {
            setRegistreringshjemler(saksdataId, emptySet(), innloggetSaksbehandler)
        }
        saksdata.ytelse = ytelse
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setMottattVedtaksinstans(saksdataId: UUID, dato: LocalDate?, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.mottattVedtaksinstans = dato
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setVedtaksinstansEnhet(saksdataId: UUID, enhetsnummer: String, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.vedtaksinstansEnhet = enhetsnummer
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setMottattKlageinstans(saksdataId: UUID, dato: LocalDate?, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.mottattKlageinstans = dato
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setUtfall(saksdataId: UUID, utfall: Utfall, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.utfall = utfall
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setRegistreringshjemler(
        saksdataId: UUID,
        registreringshjemler: Set<Registreringshjemmel>,
        innloggetSaksbehandler: String
    ): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.registreringshjemler = registreringshjemler.toMutableSet()
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun setAvsluttetAvSaksbehandler(saksdataId: UUID, innloggetSaksbehandler: String): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(saksdataId, innloggetSaksbehandler)
        saksdata.validate()
        if (saksdata.kvalitetsvurderingReference.version == 2) {
            error("v2 not supported")
        }
        if (saksdata.sakstype == Type.ANKE) {
            saksdata.mottattVedtaksinstans = null
            kvalitetsvurderingService.removeFieldsUnusedInAnke(saksdata.kvalitetsvurderingReference.id)
        }
        if (saksdata.hasKvalitetsvurdering()) {
            kvalitetsvurderingService.cleanUpKvalitetsvurdering(saksdata.kvalitetsvurderingReference.id)
        } else {
            error("There must be a kvalitetsvurdering")
        }

        saksdata.avsluttetAvSaksbehandler = LocalDateTime.now()
        saksdata.modified = LocalDateTime.now()
        return saksdata
    }

    fun reopenSaksdata(saksdataId: UUID, innloggetSaksbehandler: String, withVersion: Int? = 1): Saksdata {
        val saksdata = getSaksdataAndVerifyAccessForEdit(
            saksdataId = saksdataId,
            innloggetSaksbehandler = innloggetSaksbehandler,
            isReopen = true
        )

        when (withVersion) {
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
        saksdata.modified = LocalDateTime.now()

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

    fun searchAsVedtaksinstansleder(
        saksbehandlerIdent: String,
        enhet: Enhet,
        fromDate: LocalDate,
        toDate: LocalDate,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<Saksdata> {
        val roller = rolleMapper.toRoles(tokenUtil.getGroups())

        val kanBehandleStrengtFortrolig = ROLE_KLAGE_STRENGT_FORTROLIG in roller
        val kanBehandleFortrolig = ROLE_KLAGE_FORTROLIG in roller
        val kanBehandleEgenAnsatt = ROLE_KLAGE_EGEN_ANSATT in roller

        return saksdataRepository.findForVedtaksinstansleder(
            vedtaksinstansEnhet = enhet.navn,
            fromDateTime = fromDate.atStartOfDay(),
            toDateTime = toDate.atTime(LocalTime.MAX),
            mangelfullt = mangelfullt,
            kommentarer = kommentarer,
        ).filter {
            verifiserTilgangTilPersonForSaksbehandler(
                fnr = it.sakenGjelder ?: throw RuntimeException("missing fnr"),
                ident = saksbehandlerIdent,
                kanBehandleStrengtFortrolig = kanBehandleStrengtFortrolig,
                kanBehandleFortrolig = kanBehandleFortrolig,
                kanBehandleEgenAnsatt = kanBehandleEgenAnsatt,
            )
        }
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
                secureLogger.info("erStrengtFortrolig")
                //Merk at vi ikke sjekker egenAnsatt her, strengt fortrolig trumfer det
                if (kanBehandleStrengtFortrolig) {
                    secureLogger.info("Access granted to strengt fortrolig for $ident")
                } else {
                    secureLogger.info("Access denied to strengt fortrolig for $ident")
                    return false
                }
            }
            if (harBeskyttelsesbehovFortrolig) {
                secureLogger.info("erFortrolig")
                //Merk at vi ikke sjekker egenAnsatt her, fortrolig trumfer det
                if (kanBehandleFortrolig) {
                    secureLogger.info("Access granted to fortrolig for $ident")
                } else {
                    secureLogger.info("Access denied to fortrolig for $ident")
                    return false
                }
            }
            if (erEgenAnsatt && !(harBeskyttelsesbehovFortrolig || harBeskyttelsesbehovStrengtFortrolig)) {
                secureLogger.info("erEgenAnsatt")
                //Er kun egenAnsatt, har ikke et beskyttelsesbehov i tillegg
                if (kanBehandleEgenAnsatt) {
                    secureLogger.info("Access granted to egen ansatt for $ident")
                } else {
                    secureLogger.info("Access denied to egen ansatt for $ident")
                    return false
                }
            }
            return true
        } catch (e: Exception) {
            logger.warn("Could not verify access to person. See secure logs.")
            secureLogger.warn("Could not verify access to person", e)
            return false
        }
    }
}