package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingV1
import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingWithoutEnheterV1
import no.nav.klage.kaka.api.view.AnonymizedUnfinishedVurderingV1
import no.nav.klage.kaka.api.view.Date
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.repositories.KvalitetsvurderingV1Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.repositories.SaksdataRepositoryCustomImpl
import no.nav.klage.kaka.services.ExportServiceV1.Field.Type.*
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.*
import java.time.temporal.ChronoField
import java.util.*


@Service
class ExportServiceV1(
    private val saksdataRepository: SaksdataRepository,
    private val kvalitetsvurderingV1Repository: KvalitetsvurderingV1Repository,
) {

    /**
     * Returns excel-report, for all 'finished' saksdata (anonymized (no fnr or navIdent)). For now, only used by
     * KA-ledere.
     */
    fun getAsExcel(year: Year): ByteArray {
        val saksdataList = saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
            kvalitetsvurderingVersion = 1,
            fromDateTime = LocalDate.of(year.value, Month.JANUARY, 1).atStartOfDay(),
            toDateTime = LocalDate.of(year.value, Month.DECEMBER, 31).atTime(LocalTime.MAX),
        )

        val saksdataFields = mapToFields(saksdataList)

        val workbook = XSSFWorkbook()

        val sheet = workbook.createSheet("Uttrekk år $year")

        if (saksdataFields.isNotEmpty()) {

            //TODO: Can be calculated based on column header.
            repeat(saksdataFields.first().size) {
                sheet.setColumnWidth(it, 6000)
            }

            val header = sheet.createRow(0)
            val headerStyle = workbook.createCellStyle()

            val headerFont = workbook.createFont()
            headerFont.fontName = "Arial"

            headerFont.bold = true
            headerStyle.setFont(headerFont)

            var headerCounter = 0

            saksdataFields.first().forEach { headerColumns ->
                val headerCell = header.createCell(headerCounter++)
                headerCell.setCellValue(headerColumns.fieldName)
                headerCell.cellStyle = headerStyle
            }

            //Cells
            val createHelper = workbook.creationHelper
            var rowCounter = 1

            val cellFont = workbook.createFont()
            cellFont.fontName = "Arial"

            val cellStyleDate = workbook.createCellStyle()
            cellStyleDate.setFont(cellFont)
            cellStyleDate.dataFormat = createHelper.createDataFormat().getFormat("yyyy-mm-dd")

            val cellStyleRegular = workbook.createCellStyle()
            cellStyleRegular.setFont(cellFont)
            cellStyleRegular.wrapText = true

            saksdataFields.forEach { saksdataRow ->
                val row = sheet.createRow(rowCounter++)

                var columnCounter = 0

                saksdataRow.forEach { column ->
                    val cell = row.createCell(columnCounter++)
                    when (column.type) {
                        DATE -> {
                            if (column.value != null) {
                                cell.setCellValue((column.value as LocalDate))
                            }
                            cell.cellStyle = cellStyleDate
                        }
                        BOOLEAN -> {
                            cell.setCellValue(column.value as Boolean)
                            cell.cellStyle = cellStyleRegular
                        }
                        else -> {
                            cell.setCellValue(column.value?.toString() ?: "")
                            cell.cellStyle = cellStyleRegular
                        }
                    }
                }
            }
        }

        val baos = ByteArrayOutputStream()
        workbook.write(baos)
        return baos.toByteArray()
    }

    /**
     * Return all 'finished' saksdata for ledere based on given months and saksbehandlere. Cannot not be current month.
     */
    fun getFinishedForLederAsRawData(
        enhet: Enhet,
        fromMonth: YearMonth,
        toMonth: YearMonth,
        saksbehandlerIdentList: List<String>?
    ): List<AnonymizedFinishedVurderingV1> {
        validateNotCurrentMonth(toMonth)

        val fromDateTime = fromMonth.atDay(1).atStartOfDay()
        val toDateTime = toMonth.atEndOfMonth().atTime(LocalTime.MAX)

        val saksdataList = if (saksbehandlerIdentList == null) {
            saksdataRepository.findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                enhet = enhet.navn,
                kvalitetsvurderingVersion = 1,
                fromDateTime = fromDateTime,
                toDateTime = toDateTime,
            )
        } else {
            saksdataRepository.findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreated(
                enhet = enhet.navn,
                kvalitetsvurderingVersion = 1,
                fromDateTime = fromDateTime,
                toDateTime = toDateTime,
                saksbehandlerIdentList = saksbehandlerIdentList,
            )
        }

        return privateGetFinishedAsRawData(saksdataList = saksdataList)
    }

    /**
     * Return all 'unfinished' saksdata for ledere based on given months and saksbehandlere. Cannot not be current month.
     */
    fun getUnfinishedForLederAsRawData(
        enhet: Enhet,
        toMonth: YearMonth,
        saksbehandlerIdentList: List<String>?
    ): List<AnonymizedUnfinishedVurderingV1> {
        validateNotCurrentMonth(toMonth)

        val saksdataList = if (saksbehandlerIdentList == null) {
            saksdataRepository.findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
                enhet = enhet.navn,
                kvalitetsvurderingVersion = 1,
                toDateTime = toMonth.atEndOfMonth().atTime(LocalTime.MAX),
            )
        } else {
            saksdataRepository.findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerInOrderByCreated(
                enhet = enhet.navn,
                kvalitetsvurderingVersion = 1,
                toDateTime = toMonth.atEndOfMonth().atTime(LocalTime.MAX),
                saksbehandlerIdentList = saksbehandlerIdentList,
            )
        }

        return privateGetUnfinishedAsRawData(saksdataList = saksdataList)
    }

    private fun validateNotCurrentMonth(toMonth: YearMonth) {
        if (toMonth == YearMonth.now()) {
            throw MissingTilgangException("Cannot fetch saksdata from current month")
        }
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates
     */
    fun getFinishedAsRawDataByDates(fromDate: LocalDate, toDate: LocalDate): List<AnonymizedFinishedVurderingV1> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                kvalitetsvurderingVersion = 1,
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX)
            )
        return privateGetFinishedAsRawData(saksdataList = saksdataList)
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates
     */
    fun getFinishedAsRawDataByDatesWithoutEnheter(fromDate: LocalDate, toDate: LocalDate): List<AnonymizedFinishedVurderingWithoutEnheterV1> {
        val resultList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV1(
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX)
            )
        return privateGetFinishedAsRawDataWithoutEnheterWithResultV1(resultList = resultList)
    }

    /**
     * Return all 'finished' saksdata for vedtaksinstansleder (anonymized (no fnr or navIdent)) based on given dates
     */
    fun getFinishedAsRawDataByDatesForVedtaksinstansleder(
        fromDate: LocalDate,
        toDate: LocalDate,
        vedtaksinstansEnhet: Enhet,
        mangelfullt: List<String>,
        kommentarer: List<String>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV1> {
        val saksdataList =
            saksdataRepository.findForVedtaksinstansleder(
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX),
                vedtaksinstansEnhet = vedtaksinstansEnhet.navn,
                mangelfullt = mangelfullt,
                kommentarer = kommentarer,
            )
        return privateGetFinishedAsRawDataWithoutEnheter(saksdataList = saksdataList)
    }

    /**
     * Return 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates and saksbehandler
     */
    fun getFinishedAsRawDataByDatesAndSaksbehandler(
        fromDate: LocalDate,
        toDate: LocalDate,
        saksbehandler: String
    ): List<AnonymizedFinishedVurderingV1> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreated(
                kvalitetsvurderingVersion = 1,
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX),
                saksbehandler = saksbehandler,
            )
        return privateGetFinishedAsRawData(saksdataList = saksdataList)
    }

    /**
     * Return all 'unfinished' saksdata (anonymized (no fnr or navIdent)) based on given toDate
     */
    fun getUnfinishedAsRawDataByToDate(toDate: LocalDate): List<AnonymizedUnfinishedVurderingV1> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
                kvalitetsvurderingVersion = 1,
                toDateTime = toDate.atTime(LocalTime.MAX)
            )
        return privateGetUnfinishedAsRawData(saksdataList = saksdataList)
    }

    /**
     * Return 'unfinished' saksdata (anonymized (no fnr or navIdent)) based on given toDate and saksbehandler
     */
    fun getUnfinishedAsRawDataByToDateAndSaksbehandler(
        toDate: LocalDate,
        saksbehandler: String
    ): List<AnonymizedUnfinishedVurderingV1> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerOrderByCreated(
                kvalitetsvurderingVersion = 1,
                toDateTime = toDate.atTime(LocalTime.MAX),
                saksbehandler = saksbehandler,
            )
        return privateGetUnfinishedAsRawData(saksdataList = saksdataList)
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates
     */
    private fun privateGetFinishedAsRawData(
        saksdataList: List<Saksdata>,
    ): List<AnonymizedFinishedVurderingV1> {

        return saksdataList.map { saksdata ->
            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            if (saksdata.kvalitetsvurderingReference.version == 2) {
                error("This query only works for version 1 of kvalitetsvurderinger")
            }
            
            val kvalitetsvurderingV1 = kvalitetsvurderingV1Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)

            AnonymizedFinishedVurderingV1(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                tilknyttetEnhet = saksdata.tilknyttetEnhet,
                hjemmelIdList = saksdata.registreringshjemler!!.map { it.id },
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandlerDate,
                ytelseId = saksdata.ytelse!!.id,
                utfallId = saksdata.utfall!!.id,
                sakstypeId = saksdata.sakstype.id,
                mottattVedtaksinstans = saksdata.mottattVedtaksinstans?.toDate(),
                vedtaksinstansEnhet = saksdata.vedtaksinstansEnhet!!,
                mottattKlageinstans = mottattKlageinstansDate,
                arbeidsrettetBrukeroppfoelging = kvalitetsvurderingV1.arbeidsrettetBrukeroppfoelging,
                begrunnelseForHvorforAvslagOpprettholdes = kvalitetsvurderingV1.begrunnelseForHvorforAvslagOpprettholdes,
                begrunnelsenErIkkeKonkretOgIndividuell = kvalitetsvurderingV1.begrunnelsenErIkkeKonkretOgIndividuell,
                betydeligAvvik = kvalitetsvurderingV1.betydeligAvvik,
                brukIOpplaering = kvalitetsvurderingV1.brukIOpplaering,
                detErFeilIKonkretRettsanvendelse = kvalitetsvurderingV1.detErFeilIKonkretRettsanvendelse,
                detErIkkeBruktRiktigHjemmel = kvalitetsvurderingV1.detErIkkeBruktRiktigHjemmel,
                innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = kvalitetsvurderingV1.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                klagerensRelevanteAnfoerslerIkkeKommentert = kvalitetsvurderingV1.klagerensRelevanteAnfoerslerIkkeKommentert,
                konklusjonen = kvalitetsvurderingV1.konklusjonen,
                nyeOpplysningerMottatt = kvalitetsvurderingV1.nyeOpplysningerMottatt,
                oversendelsesbrevetsInnholdIkkeISamsvarMedTema = kvalitetsvurderingV1.oversendelsesbrevetsInnholdIkkeISamsvarMedTema,
                oversittetKlagefristIkkeKommentert = kvalitetsvurderingV1.oversittetKlagefristIkkeKommentert,
                raadgivendeLegeErBruktFeilSpoersmaal = kvalitetsvurderingV1.raadgivendeLegeErBruktFeilSpoersmaal,
                raadgivendeLegeErBruktMangelfullDokumentasjon = kvalitetsvurderingV1.raadgivendeLegeErBruktMangelfullDokumentasjon,
                raadgivendeLegeErIkkeBrukt = kvalitetsvurderingV1.raadgivendeLegeErIkkeBrukt,
                raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = kvalitetsvurderingV1.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin,
                rettsregelenErBenyttetFeil = kvalitetsvurderingV1.rettsregelenErBenyttetFeil,
                sakensDokumenter = kvalitetsvurderingV1.sakensDokumenter,
                spraaketErIkkeTydelig = kvalitetsvurderingV1.spraaketErIkkeTydelig,
                utredningenAvAndreAktuelleForholdISaken = kvalitetsvurderingV1.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvArbeid = kvalitetsvurderingV1.utredningenAvArbeid,
                utredningenAvEoesProblematikk = kvalitetsvurderingV1.utredningenAvEoesProblematikk,
                utredningenAvInntektsforhold = kvalitetsvurderingV1.utredningenAvInntektsforhold,
                utredningenAvMedisinskeForhold = kvalitetsvurderingV1.utredningenAvMedisinskeForhold,
                veiledningFraNav = kvalitetsvurderingV1.veiledningFraNav,
                vurderingAvFaktumErMangelfull = kvalitetsvurderingV1.vurderingAvFaktumErMangelfull,
                klageforberedelsenRadioValg = kvalitetsvurderingV1.klageforberedelsenRadioValg?.name,
                utredningenRadioValg = kvalitetsvurderingV1.utredningenRadioValg?.name,
                vedtaketRadioValg = kvalitetsvurderingV1.vedtaketRadioValg?.name,
                brukAvRaadgivendeLegeRadioValg = kvalitetsvurderingV1.brukAvRaadgivendeLegeRadioValg?.name,
                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata),
                modifiedDate = getModifiedDate(saksdata),
            )
        }
    }

    private fun privateGetFinishedAsRawDataWithoutEnheterWithResultV1(
        resultList: List<SaksdataRepositoryCustomImpl.ResultV1>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV1> {
        
        return resultList.map { result ->
            val saksdata = result.saksdata
            val kvalitetsvurdering = result.kvalitetsvurdering

            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay
            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            if (saksdata.kvalitetsvurderingReference.version == 2) {
                error("Don't support v2 yet")
            }

            AnonymizedFinishedVurderingWithoutEnheterV1(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                hjemmelIdList = saksdata.registreringshjemler!!.map { it.id },
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandlerDate,
                ytelseId = saksdata.ytelse!!.id,
                utfallId = saksdata.utfall!!.id,
                sakstypeId = saksdata.sakstype.id,
                mottattVedtaksinstans = saksdata.mottattVedtaksinstans?.toDate(),
                mottattKlageinstans = mottattKlageinstansDate,
                arbeidsrettetBrukeroppfoelging = kvalitetsvurdering.arbeidsrettetBrukeroppfoelging,
                begrunnelseForHvorforAvslagOpprettholdes = kvalitetsvurdering.begrunnelseForHvorforAvslagOpprettholdes,
                begrunnelsenErIkkeKonkretOgIndividuell = kvalitetsvurdering.begrunnelsenErIkkeKonkretOgIndividuell,
                betydeligAvvik = kvalitetsvurdering.betydeligAvvik,
                brukIOpplaering = kvalitetsvurdering.brukIOpplaering,
                detErFeilIKonkretRettsanvendelse = kvalitetsvurdering.detErFeilIKonkretRettsanvendelse,
                detErIkkeBruktRiktigHjemmel = kvalitetsvurdering.detErIkkeBruktRiktigHjemmel,
                innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = kvalitetsvurdering.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                klagerensRelevanteAnfoerslerIkkeKommentert = kvalitetsvurdering.klagerensRelevanteAnfoerslerIkkeKommentert,
                konklusjonen = kvalitetsvurdering.konklusjonen,
                nyeOpplysningerMottatt = kvalitetsvurdering.nyeOpplysningerMottatt,
                oversendelsesbrevetsInnholdIkkeISamsvarMedTema = kvalitetsvurdering.oversendelsesbrevetsInnholdIkkeISamsvarMedTema,
                oversittetKlagefristIkkeKommentert = kvalitetsvurdering.oversittetKlagefristIkkeKommentert,
                raadgivendeLegeErBruktFeilSpoersmaal = kvalitetsvurdering.raadgivendeLegeErBruktFeilSpoersmaal,
                raadgivendeLegeErBruktMangelfullDokumentasjon = kvalitetsvurdering.raadgivendeLegeErBruktMangelfullDokumentasjon,
                raadgivendeLegeErIkkeBrukt = kvalitetsvurdering.raadgivendeLegeErIkkeBrukt,
                raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = kvalitetsvurdering.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin,
                rettsregelenErBenyttetFeil = kvalitetsvurdering.rettsregelenErBenyttetFeil,
                sakensDokumenter = kvalitetsvurdering.sakensDokumenter,
                spraaketErIkkeTydelig = kvalitetsvurdering.spraaketErIkkeTydelig,
                utredningenAvAndreAktuelleForholdISaken = kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvArbeid = kvalitetsvurdering.utredningenAvArbeid,
                utredningenAvEoesProblematikk = kvalitetsvurdering.utredningenAvEoesProblematikk,
                utredningenAvInntektsforhold = kvalitetsvurdering.utredningenAvInntektsforhold,
                utredningenAvMedisinskeForhold = kvalitetsvurdering.utredningenAvMedisinskeForhold,
                veiledningFraNav = kvalitetsvurdering.veiledningFraNav,
                vurderingAvFaktumErMangelfull = kvalitetsvurdering.vurderingAvFaktumErMangelfull,
                klageforberedelsenRadioValg = kvalitetsvurdering.klageforberedelsenRadioValg?.name,
                utredningenRadioValg = kvalitetsvurdering.utredningenRadioValg?.name,
                vedtaketRadioValg = kvalitetsvurdering.vedtaketRadioValg?.name,
                brukAvRaadgivendeLegeRadioValg = kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg?.name,
                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata),
                modifiedDate = getModifiedDate(saksdata),
            )
        }
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr, navIdent or enheter)) based on given dates.
     */
    private fun privateGetFinishedAsRawDataWithoutEnheter(
        saksdataList: List<Saksdata>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV1> {

        return saksdataList.map { saksdata ->
            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay
            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            val kvalitetsvurderingV1 = kvalitetsvurderingV1Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)

            AnonymizedFinishedVurderingWithoutEnheterV1(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                hjemmelIdList = saksdata.registreringshjemler!!.map { it.id },
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandlerDate,
                ytelseId = saksdata.ytelse!!.id,
                utfallId = saksdata.utfall!!.id,
                sakstypeId = saksdata.sakstype.id,
                mottattVedtaksinstans = saksdata.mottattVedtaksinstans?.toDate(),
                mottattKlageinstans = mottattKlageinstansDate,
                arbeidsrettetBrukeroppfoelging = kvalitetsvurderingV1.arbeidsrettetBrukeroppfoelging,
                begrunnelseForHvorforAvslagOpprettholdes = kvalitetsvurderingV1.begrunnelseForHvorforAvslagOpprettholdes,
                begrunnelsenErIkkeKonkretOgIndividuell = kvalitetsvurderingV1.begrunnelsenErIkkeKonkretOgIndividuell,
                betydeligAvvik = kvalitetsvurderingV1.betydeligAvvik,
                brukIOpplaering = kvalitetsvurderingV1.brukIOpplaering,
                detErFeilIKonkretRettsanvendelse = kvalitetsvurderingV1.detErFeilIKonkretRettsanvendelse,
                detErIkkeBruktRiktigHjemmel = kvalitetsvurderingV1.detErIkkeBruktRiktigHjemmel,
                innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = kvalitetsvurderingV1.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                klagerensRelevanteAnfoerslerIkkeKommentert = kvalitetsvurderingV1.klagerensRelevanteAnfoerslerIkkeKommentert,
                konklusjonen = kvalitetsvurderingV1.konklusjonen,
                nyeOpplysningerMottatt = kvalitetsvurderingV1.nyeOpplysningerMottatt,
                oversendelsesbrevetsInnholdIkkeISamsvarMedTema = kvalitetsvurderingV1.oversendelsesbrevetsInnholdIkkeISamsvarMedTema,
                oversittetKlagefristIkkeKommentert = kvalitetsvurderingV1.oversittetKlagefristIkkeKommentert,
                raadgivendeLegeErBruktFeilSpoersmaal = kvalitetsvurderingV1.raadgivendeLegeErBruktFeilSpoersmaal,
                raadgivendeLegeErBruktMangelfullDokumentasjon = kvalitetsvurderingV1.raadgivendeLegeErBruktMangelfullDokumentasjon,
                raadgivendeLegeErIkkeBrukt = kvalitetsvurderingV1.raadgivendeLegeErIkkeBrukt,
                raadgivendeLegeHarUttaltSegUtoverTrygdemedisin = kvalitetsvurderingV1.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin,
                rettsregelenErBenyttetFeil = kvalitetsvurderingV1.rettsregelenErBenyttetFeil,
                sakensDokumenter = kvalitetsvurderingV1.sakensDokumenter,
                spraaketErIkkeTydelig = kvalitetsvurderingV1.spraaketErIkkeTydelig,
                utredningenAvAndreAktuelleForholdISaken = kvalitetsvurderingV1.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvArbeid = kvalitetsvurderingV1.utredningenAvArbeid,
                utredningenAvEoesProblematikk = kvalitetsvurderingV1.utredningenAvEoesProblematikk,
                utredningenAvInntektsforhold = kvalitetsvurderingV1.utredningenAvInntektsforhold,
                utredningenAvMedisinskeForhold = kvalitetsvurderingV1.utredningenAvMedisinskeForhold,
                veiledningFraNav = kvalitetsvurderingV1.veiledningFraNav,
                vurderingAvFaktumErMangelfull = kvalitetsvurderingV1.vurderingAvFaktumErMangelfull,
                klageforberedelsenRadioValg = kvalitetsvurderingV1.klageforberedelsenRadioValg?.name,
                utredningenRadioValg = kvalitetsvurderingV1.utredningenRadioValg?.name,
                vedtaketRadioValg = kvalitetsvurderingV1.vedtaketRadioValg?.name,
                brukAvRaadgivendeLegeRadioValg = kvalitetsvurderingV1.brukAvRaadgivendeLegeRadioValg?.name,
                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata),
                modifiedDate = getModifiedDate(saksdata),
            )
        }
    }

    private fun getVedtaksinstansBehandlingstidDays(saksdata: Saksdata): Int {
        return if (saksdata.sakstype == Type.KLAGE) {
            saksdata.mottattKlageinstans!!.toDate().epochDay - saksdata.mottattVedtaksinstans!!.toEpochDay().toInt()
        } else {
            //FE wants 0 for anker as of now.
            0
        }
    }

    private fun getMottattForrigeInstans(saksdata: Saksdata): Date {
        val mottattForrigeInstans = if (saksdata.sakstype == Type.ANKE) {
            saksdata.mottattKlageinstans!!.toDate()
        } else {
            saksdata.mottattVedtaksinstans!!.toDate()
        }
        return mottattForrigeInstans
    }

    private fun getCreatedDate(saksdata: Saksdata): Date {
        val kvalitetsvurderingV1 = kvalitetsvurderingV1Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)
        return if (saksdata.created.isBefore(kvalitetsvurderingV1.created)) {
            saksdata.created.toDate()
        } else {
            kvalitetsvurderingV1.created.toDate()
        }
    }

    private fun getModifiedDate(saksdata: Saksdata): Date {
        val kvalitetsvurderingV1 = kvalitetsvurderingV1Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)
        return if (saksdata.modified.isAfter(kvalitetsvurderingV1.modified)) {
            saksdata.modified.toDate()
        } else {
            kvalitetsvurderingV1.modified.toDate()
        }
    }

    /**
     * Return all 'unfinished' saksdata (anonymized (no fnr or navIdent)) based on given toDate
     */
    private fun privateGetUnfinishedAsRawData(saksdataList: List<Saksdata>): List<AnonymizedUnfinishedVurderingV1> {
        return saksdataList.map { saksdata ->
            AnonymizedUnfinishedVurderingV1(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                tilknyttetEnhet = saksdata.tilknyttetEnhet,
                sakstypeId = saksdata.sakstype.id,
                createdDate = getCreatedDate(saksdata),
                modifiedDate = getModifiedDate(saksdata),
            )
        }
    }

    private fun mapToFields(saksdataList: List<Saksdata>): List<List<Field>> {
        //@formatter:off
        return saksdataList.map { saksdata ->
            val kvalitetsvurderingV1 = kvalitetsvurderingV1Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)
            buildList {
                //Saksdata
                add(Field(fieldName = "Tilknyttet enhet", value = saksdata.tilknyttetEnhet, type = STRING))
                add(Field(fieldName = "Sakstype", value = saksdata.sakstype.navn, type = STRING))
                add(Field(fieldName = "Ytelse", value = saksdata.ytelse!!.navn, type = STRING))
                add(Field(fieldName = "Mottatt vedtaksinstans", value = saksdata.mottattVedtaksinstans, type = DATE))
                add(Field(fieldName = "Mottatt klageinstans", value = saksdata.mottattKlageinstans, type = DATE))
                add(Field(fieldName = "Ferdigstilt", value = saksdata.avsluttetAvSaksbehandler?.toLocalDate(), type = DATE))
                add(Field(fieldName = "Fra vedtaksenhet", value = saksdata.vedtaksinstansEnhet, type = STRING))
                add(Field(fieldName = "Utfall/Resultat", value = saksdata.utfall!!.navn, type = STRING))
                add(Field(fieldName = "Hjemmel", value = saksdata.registreringshjemler.toHjemlerString(), type = STRING))

                //Klageforberedelsen
                add(Field(fieldName = "Klageforberedelsen", value = kvalitetsvurderingV1.klageforberedelsenRadioValg, type = STRING))
                add(Field(fieldName = "Sakens dokumenter", value = kvalitetsvurderingV1.sakensDokumenter, type = BOOLEAN))
                add(Field(fieldName = "Oversittet klagefrist er ikke kommentert", value = kvalitetsvurderingV1.oversittetKlagefristIkkeKommentert, type = BOOLEAN))
                add(Field(fieldName = "Klagerens relevante anførseler er ikke tilstrekkelig kommentert/imøtegått", value = kvalitetsvurderingV1.klagerensRelevanteAnfoerslerIkkeKommentert, type = BOOLEAN))
                add(Field(fieldName = "Begrunnelse for hvorfor avslag opprettholdes / klager ikke oppfyller vilkår", value = kvalitetsvurderingV1.begrunnelseForHvorforAvslagOpprettholdes, type = BOOLEAN))
                add(Field(fieldName = "Konklusjonen", value = kvalitetsvurderingV1.konklusjonen, type = BOOLEAN))
                add(Field(fieldName = "Oversendelsesbrevets innhold er ikke i samsvar med sakens tema", value = kvalitetsvurderingV1.oversendelsesbrevetsInnholdIkkeISamsvarMedTema, type = BOOLEAN))

                //Utredningen
                add(Field(fieldName = "Utredningen", value = kvalitetsvurderingV1.utredningenRadioValg, type = STRING))
                add(Field(fieldName = "Utredningen av medisinske forhold", value = kvalitetsvurderingV1.utredningenAvMedisinskeForhold, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av medisinske forhold stikkord", value = kvalitetsvurderingV1.utredningenAvMedisinskeForholdText, type = STRING))
                add(Field(fieldName = "Utredningen av inntektsforhold", value = kvalitetsvurderingV1.utredningenAvInntektsforhold, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av inntektsforhold stikkord", value = kvalitetsvurderingV1.utredningenAvInntektsforholdText, type = STRING))
                add(Field(fieldName = "Utredningen av arbeid", value = kvalitetsvurderingV1.utredningenAvArbeid, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av arbeid stikkord", value = kvalitetsvurderingV1.utredningenAvArbeidText, type = STRING))
                add(Field(fieldName = "Arbeidsrettet brukeroppfølging", value = kvalitetsvurderingV1.arbeidsrettetBrukeroppfoelging, type = BOOLEAN))
                add(Field(fieldName = "Arbeidsrettet brukeroppfølging stikkord", value = kvalitetsvurderingV1.arbeidsrettetBrukeroppfoelgingText, type = STRING))
                add(Field(fieldName = "Utredningen av andre aktuelle forhold i saken", value = kvalitetsvurderingV1.utredningenAvAndreAktuelleForholdISaken, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av andre aktuelle forhold i saken stikkord", value = kvalitetsvurderingV1.utredningenAvAndreAktuelleForholdISakenText, type = STRING))
                add(Field(fieldName = "Utredningen av EØS / utenlandsproblematikk", value = kvalitetsvurderingV1.utredningenAvEoesProblematikk, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av EØS / utenlandsproblematikk stikkord", value = kvalitetsvurderingV1.utredningenAvEoesProblematikkText, type = STRING))
                add(Field(fieldName = "Veiledning fra NAV", value = kvalitetsvurderingV1.veiledningFraNav, type = BOOLEAN))
                add(Field(fieldName = "Veiledning fra NAV stikkord", value = kvalitetsvurderingV1.veiledningFraNavText, type = STRING))

                //Vedtaket
                add(Field(fieldName = "Vedtaket", value = kvalitetsvurderingV1.vedtaketRadioValg, type = STRING))
                add(Field(fieldName = "Det er ikke brukt riktig hjemmel(er)", value = kvalitetsvurderingV1.detErIkkeBruktRiktigHjemmel, type = BOOLEAN))
                add(Field(fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet", value = kvalitetsvurderingV1.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet, type = BOOLEAN))
                add(Field(fieldName = "Rettsregelen er benyttet eller tolket feil", value = kvalitetsvurderingV1.rettsregelenErBenyttetFeil, type = BOOLEAN))
                add(Field(fieldName = "Vurdering av faktum / bevisvurdering er mangelfull", value = kvalitetsvurderingV1.vurderingAvFaktumErMangelfull, type = BOOLEAN))
                add(Field(fieldName = "Det er feil i den konkrete rettsanvendelsen", value = kvalitetsvurderingV1.detErFeilIKonkretRettsanvendelse, type = BOOLEAN))
                add(Field(fieldName = "Begrunnelsen er ikke konkret og individuell", value = kvalitetsvurderingV1.begrunnelsenErIkkeKonkretOgIndividuell, type = BOOLEAN))
                add(Field(fieldName = "Språket/Formidlingen er ikke tydelig", value = kvalitetsvurderingV1.spraaketErIkkeTydelig, type = BOOLEAN))

                //Annet
                add(Field(fieldName = "Nye opplysninger mottatt etter oversendelse til klageinstansen", value = kvalitetsvurderingV1.nyeOpplysningerMottatt, type = BOOLEAN))
                add(Field(fieldName = "Bruk gjerne vedtaket som eksempel i opplæring", value = kvalitetsvurderingV1.brukIOpplaering, type = BOOLEAN))
                add(Field(fieldName = "Bruk gjerne vedtaket som eksempel i opplæring stikkord", value = kvalitetsvurderingV1.brukIOpplaeringText, type = STRING))

                //ROL
                add(Field(fieldName = "Bruk av rådgivende lege", value = kvalitetsvurderingV1.brukAvRaadgivendeLegeRadioValg, type = STRING))
                add(Field(fieldName = "Rådgivende lege er ikke brukt", value = kvalitetsvurderingV1.raadgivendeLegeErIkkeBrukt, type = BOOLEAN))
                add(Field(fieldName = "Rådgivende lege er brukt, men saksbehandler har stilt feil spørsmål og får derfor feil svar", value = kvalitetsvurderingV1.raadgivendeLegeErBruktFeilSpoersmaal, type = BOOLEAN))
                add(Field(fieldName = "Rådgivende lege har uttalt seg om tema utover trygdemedisin", value = kvalitetsvurderingV1.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin, type = BOOLEAN))
                add(Field(fieldName = "Rådgivende lege er brukt, men dokumentasjonen er mangelfull / ikke skriftliggjort", value = kvalitetsvurderingV1.raadgivendeLegeErBruktMangelfullDokumentasjon, type = BOOLEAN))

            //@formatter:on
            }
        }
    }

    private fun Set<Registreringshjemmel>?.toHjemlerString() =
        this?.joinToString { "${it.lovKilde.beskrivelse} - ${it.spesifikasjon}" } ?: ""

    data class Field(val fieldName: String, val value: Any?, val type: Type) {
        enum class Type {
            STRING, NUMBER, BOOLEAN, DATE
        }
    }

}

private fun LocalDateTime.toDate(): Date {
    return Date(
        weekNumber = this.get(ChronoField.ALIGNED_WEEK_OF_YEAR),
        year = this.year,
        month = this.monthValue,
        day = this.dayOfMonth,
        iso = this.toLocalDate().toString(),
        epochDay = this.toLocalDate().toEpochDay().toInt()
    )
}

private fun LocalDate.toDate(): Date {
    return Date(
        weekNumber = this.get(ChronoField.ALIGNED_WEEK_OF_YEAR),
        year = this.year,
        month = this.monthValue,
        day = this.dayOfMonth,
        iso = this.toString(),
        epochDay = this.toEpochDay().toInt()
    )
}