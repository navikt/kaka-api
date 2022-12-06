package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingV2
import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingWithoutEnheterV2
import no.nav.klage.kaka.api.view.AnonymizedUnfinishedVurderingV2
import no.nav.klage.kaka.api.view.Date
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.repositories.KvalitetsvurderingV2Repository
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoField
import java.util.*


@Service
class ExportServiceV2(
    private val saksdataRepository: SaksdataRepository,
    private val kvalitetsvurderingV2Repository: KvalitetsvurderingV2Repository,
) {

    /**
     * Returns excel-report, for all 'finished' saksdata (anonymized (no fnr or navIdent)). For now, only used by
     * KA-ledere.
     */
//    fun getAsExcel(year: Year): ByteArray {
//        val saksdataList = saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
//            kvalitetsvurderingVersion = 2,
//            fromDateTime = LocalDate.of(year.value, Month.JANUARY, 1).atStartOfDay(),
//            toDateTime = LocalDate.of(year.value, Month.DECEMBER, 31).atTime(LocalTime.MAX),
//        )
//
//        val saksdataFields = mapToFields(saksdataList)
//
//        val workbook = XSSFWorkbook()
//
//        val sheet = workbook.createSheet("Uttrekk år $year")
//
//        if (saksdataFields.isNotEmpty()) {
//
//            //TODO: Can be calculated based on column header.
//            repeat(saksdataFields.first().size) {
//                sheet.setColumnWidth(it, 6000)
//            }
//
//            val header = sheet.createRow(0)
//            val headerStyle = workbook.createCellStyle()
//
//            val headerFont = workbook.createFont()
//            headerFont.fontName = "Arial"
//
//            headerFont.bold = true
//            headerStyle.setFont(headerFont)
//
//            var headerCounter = 0
//
//            saksdataFields.first().forEach { headerColumns ->
//                val headerCell = header.createCell(headerCounter++)
//                headerCell.setCellValue(headerColumns.fieldName)
//                headerCell.cellStyle = headerStyle
//            }
//
//            //Cells
//            val createHelper = workbook.creationHelper
//            var rowCounter = 1
//
//            val cellFont = workbook.createFont()
//            cellFont.fontName = "Arial"
//
//            val cellStyleDate = workbook.createCellStyle()
//            cellStyleDate.setFont(cellFont)
//            cellStyleDate.dataFormat = createHelper.createDataFormat().getFormat("yyyy-mm-dd")
//
//            val cellStyleRegular = workbook.createCellStyle()
//            cellStyleRegular.setFont(cellFont)
//            cellStyleRegular.wrapText = true
//
//            saksdataFields.forEach { saksdataRow ->
//                val row = sheet.createRow(rowCounter++)
//
//                var columnCounter = 0
//
//                saksdataRow.forEach { column ->
//                    val cell = row.createCell(columnCounter++)
//                    when (column.type) {
//                        DATE -> {
//                            if (column.value != null) {
//                                cell.setCellValue((column.value as LocalDate))
//                            }
//                            cell.cellStyle = cellStyleDate
//                        }
//
//                        BOOLEAN -> {
//                            cell.setCellValue(column.value as Boolean)
//                            cell.cellStyle = cellStyleRegular
//                        }
//
//                        else -> {
//                            cell.setCellValue(column.value?.toString() ?: "")
//                            cell.cellStyle = cellStyleRegular
//                        }
//                    }
//                }
//            }
//        }
//
//        val baos = ByteArrayOutputStream()
//        workbook.write(baos)
//        return baos.toByteArray()
//    }

    /**
     * Return all 'finished' saksdata for ledere based on given months and saksbehandlere. Cannot not be current month.
     */
    fun getFinishedForLederAsRawData(
        enhet: Enhet,
        fromMonth: YearMonth,
        toMonth: YearMonth,
        saksbehandlerIdentList: List<String>?
    ): List<AnonymizedFinishedVurderingV2> {
        validateNotCurrentMonth(toMonth)

        val fromDateTime = fromMonth.atDay(1).atStartOfDay()
        val toDateTime = toMonth.atEndOfMonth().atTime(LocalTime.MAX)

        val saksdataList = if (saksbehandlerIdentList == null) {
            saksdataRepository.findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                enhet = enhet.navn,
                kvalitetsvurderingVersion = 2,
                fromDateTime = fromDateTime,
                toDateTime = toDateTime,
            )
        } else {
            saksdataRepository.findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerInOrderByCreated(
                enhet = enhet.navn,
                kvalitetsvurderingVersion = 2,
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
    ): List<AnonymizedUnfinishedVurderingV2> {
        validateNotCurrentMonth(toMonth)

        val saksdataList = if (saksbehandlerIdentList == null) {
            saksdataRepository.findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
                enhet = enhet.navn,
                kvalitetsvurderingVersion = 2,
                toDateTime = toMonth.atEndOfMonth().atTime(LocalTime.MAX),
            )
        } else {
            saksdataRepository.findByTilknyttetEnhetAndKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerInOrderByCreated(
                enhet = enhet.navn,
                kvalitetsvurderingVersion = 2,
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
    fun getFinishedAsRawDataByDates(fromDate: LocalDate, toDate: LocalDate): List<AnonymizedFinishedVurderingV2> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                kvalitetsvurderingVersion = 2,
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX)
            )
        return privateGetFinishedAsRawData(saksdataList = saksdataList)
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates
     */
    fun getFinishedAsRawDataByDatesWithoutEnheter(
        fromDate: LocalDate,
        toDate: LocalDate
    ): List<AnonymizedFinishedVurderingWithoutEnheterV2> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                kvalitetsvurderingVersion = 2,
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX)
            )
        return privateGetFinishedAsRawDataWithoutEnheter(saksdataList = saksdataList)
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
    ): List<AnonymizedFinishedVurderingWithoutEnheterV2> {
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
    ): List<AnonymizedFinishedVurderingV2> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerBetweenAndUtfoerendeSaksbehandlerOrderByCreated(
                kvalitetsvurderingVersion = 2,
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX),
                saksbehandler = saksbehandler,
            )
        return privateGetFinishedAsRawData(saksdataList = saksdataList)
    }

    /**
     * Return all 'unfinished' saksdata (anonymized (no fnr or navIdent)) based on given toDate
     */
    fun getUnfinishedAsRawDataByToDate(toDate: LocalDate): List<AnonymizedUnfinishedVurderingV2> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanOrderByCreated(
                kvalitetsvurderingVersion = 2,
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
    ): List<AnonymizedUnfinishedVurderingV2> {
        val saksdataList =
            saksdataRepository.findByKvalitetsvurderingReferenceVersionAndAvsluttetAvSaksbehandlerIsNullAndCreatedLessThanAndUtfoerendeSaksbehandlerOrderByCreated(
                kvalitetsvurderingVersion = 2,
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
    ): List<AnonymizedFinishedVurderingV2> {

        return saksdataList.map { saksdata ->
            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            if (saksdata.kvalitetsvurderingReference.version == 1) {
                error("This query only works for version 2 of kvalitetsvurderinger")
            }

            val kvalitetsvurderingV2 =
                kvalitetsvurderingV2Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)

            AnonymizedFinishedVurderingV2(
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

                sakensDokumenter = kvalitetsvurderingV2.sakensDokumenter,
                sakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = kvalitetsvurderingV2.sakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                sakensDokumenterJournalfoerteDokumenterFeilNavn = kvalitetsvurderingV2.sakensDokumenterJournalfoerteDokumenterFeilNavn,
                sakensDokumenterManglerFysiskSaksmappe = kvalitetsvurderingV2.sakensDokumenterManglerFysiskSaksmappe,
                klageforberedelsen = kvalitetsvurderingV2.klageforberedelsen?.name,
                klageforberedelsenUnderinstansIkkeSendtAlleRelevanteSaksdokumenterTilParten = kvalitetsvurderingV2.klageforberedelsenUnderinstansIkkeSendtAlleRelevanteSaksdokumenterTilParten,
                klageforberedelsenOversittetKlagefristIkkeKommentert = kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligImotegatt = kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligImotegatt,
                klageforberedelsenMangelfullBegrunnelseForHvorforVedtaketOpprettholdes = kvalitetsvurderingV2.klageforberedelsenMangelfullBegrunnelseForHvorforVedtaketOpprettholdes,
                klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                utredningen = kvalitetsvurderingV2.utredningen?.name,
                utredningenAvMedisinskeForhold = kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                utredningenAvInntektsforhold = kvalitetsvurderingV2.utredningenAvInntektsforhold,
                utredningenAvArbeidsaktivitet = kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                utredningenAvEoesUtenlandsproblematikk = kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                utredningenAvAndreAktuelleForholdISaken = kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                vedtaketLovbestemmelsenTolketFeil = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                vedtaketLovbestemmelsenTolketFeilHjemlerList = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketFeilKonkretRettsanvendelse = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                vedtaketFeilKonkretRettsanvendelseHjemlerList = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id },
                vedtaketIkkeKonkretIndividuellBegrunnelse = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                vedtaketIkkeGodtNokFremFaktum = kvalitetsvurderingV2.vedtaketIkkeGodtNokFremFaktum,
                vedtaketIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = kvalitetsvurderingV2.vedtaketIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                vedtaketMyeStandardtekst = kvalitetsvurderingV2.vedtaketMyeStandardtekst,
                vedtakAutomatiskVedtak = kvalitetsvurderingV2.vedtakAutomatiskVedtak,
                vedtaket = kvalitetsvurderingV2.vedtaket?.name,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                vedtaketDetErLagtTilGrunnFeilFaktum = kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                vedtaketSpraakOgFormidlingErIkkeTydelig = kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                raadgivendeLegeIkkebrukt = kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeSkriftliggjort = kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeSkriftliggjort,
                brukAvRaadgivendeLege = kvalitetsvurderingV2.brukAvRaadgivendeLege?.name,
                annetFritekst = kvalitetsvurderingV2.annetFritekst,

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
    ): List<AnonymizedFinishedVurderingWithoutEnheterV2> {

        return saksdataList.map { saksdata ->
            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay
            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            if (saksdata.kvalitetsvurderingReference.version == 1) {
                error("This query only works for version 2 of kvalitetsvurderinger")
            }

            val kvalitetsvurderingV2 =
                kvalitetsvurderingV2Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)

            AnonymizedFinishedVurderingWithoutEnheterV2(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                hjemmelIdList = saksdata.registreringshjemler!!.map { it.id },
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandlerDate,
                ytelseId = saksdata.ytelse!!.id,
                utfallId = saksdata.utfall!!.id,
                sakstypeId = saksdata.sakstype.id,
                mottattVedtaksinstans = saksdata.mottattVedtaksinstans?.toDate(),
                mottattKlageinstans = mottattKlageinstansDate,

                sakensDokumenter = kvalitetsvurderingV2.sakensDokumenter,
                sakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = kvalitetsvurderingV2.sakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                sakensDokumenterJournalfoerteDokumenterFeilNavn = kvalitetsvurderingV2.sakensDokumenterJournalfoerteDokumenterFeilNavn,
                sakensDokumenterManglerFysiskSaksmappe = kvalitetsvurderingV2.sakensDokumenterManglerFysiskSaksmappe,
                klageforberedelsen = kvalitetsvurderingV2.klageforberedelsen?.name,
                klageforberedelsenUnderinstansIkkeSendtAlleRelevanteSaksdokumenterTilParten = kvalitetsvurderingV2.klageforberedelsenUnderinstansIkkeSendtAlleRelevanteSaksdokumenterTilParten,
                klageforberedelsenOversittetKlagefristIkkeKommentert = kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligImotegatt = kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligImotegatt,
                klageforberedelsenMangelfullBegrunnelseForHvorforVedtaketOpprettholdes = kvalitetsvurderingV2.klageforberedelsenMangelfullBegrunnelseForHvorforVedtaketOpprettholdes,
                klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                utredningen = kvalitetsvurderingV2.utredningen?.name,
                utredningenAvMedisinskeForhold = kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                utredningenAvInntektsforhold = kvalitetsvurderingV2.utredningenAvInntektsforhold,
                utredningenAvArbeidsaktivitet = kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                utredningenAvEoesUtenlandsproblematikk = kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                utredningenAvAndreAktuelleForholdISaken = kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                vedtaketLovbestemmelsenTolketFeil = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                vedtaketLovbestemmelsenTolketFeilHjemlerList = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketFeilKonkretRettsanvendelse = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                vedtaketFeilKonkretRettsanvendelseHjemlerList = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id },
                vedtaketIkkeKonkretIndividuellBegrunnelse = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                vedtaketIkkeGodtNokFremFaktum = kvalitetsvurderingV2.vedtaketIkkeGodtNokFremFaktum,
                vedtaketIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = kvalitetsvurderingV2.vedtaketIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                vedtaketMyeStandardtekst = kvalitetsvurderingV2.vedtaketMyeStandardtekst,
                vedtakAutomatiskVedtak = kvalitetsvurderingV2.vedtakAutomatiskVedtak,
                vedtaket = kvalitetsvurderingV2.vedtaket?.name,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                vedtaketDetErLagtTilGrunnFeilFaktum = kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                vedtaketSpraakOgFormidlingErIkkeTydelig = kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                raadgivendeLegeIkkebrukt = kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeSkriftliggjort = kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeSkriftliggjort,
                brukAvRaadgivendeLege = kvalitetsvurderingV2.brukAvRaadgivendeLege?.name,
                annetFritekst = kvalitetsvurderingV2.annetFritekst,

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
        if (saksdata.kvalitetsvurderingReference.version == 2) {
            error("Don't support v2 yet")
        }

        val kvalitetsvurderingV2 =
            kvalitetsvurderingV2Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)
        return if (saksdata.created.isBefore(kvalitetsvurderingV2.created)) {
            saksdata.created.toDate()
        } else {
            kvalitetsvurderingV2.created.toDate()
        }
    }

    private fun getModifiedDate(saksdata: Saksdata): Date {
        if (saksdata.kvalitetsvurderingReference.version == 2) {
            error("Don't support v2 yet")
        }

        val kvalitetsvurderingV2 =
            kvalitetsvurderingV2Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)
        return if (saksdata.modified.isAfter(kvalitetsvurderingV2.modified)) {
            saksdata.modified.toDate()
        } else {
            kvalitetsvurderingV2.modified.toDate()
        }
    }

    /**
     * Return all 'unfinished' saksdata (anonymized (no fnr or navIdent)) based on given toDate
     */
    private fun privateGetUnfinishedAsRawData(saksdataList: List<Saksdata>): List<AnonymizedUnfinishedVurderingV2> {
        return saksdataList.map { saksdata ->
            AnonymizedUnfinishedVurderingV2(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                tilknyttetEnhet = saksdata.tilknyttetEnhet,
                sakstypeId = saksdata.sakstype.id,
                createdDate = getCreatedDate(saksdata),
                modifiedDate = getModifiedDate(saksdata),
            )
        }
    }

//    private fun mapToFields(saksdataList: List<Saksdata>): List<List<Field>> {
//        //@formatter:off
//        return saksdataList.map { saksdata ->
        //    if (saksdata.kvalitetsvurderingReference.version == 1) {
        //        error("This query only works for version 2 of kvalitetsvurderinger")
        //    }
//
//            val kvalitetsvurderingV2 =
//                kvalitetsvurderingV2Repository.getReferenceById(saksdata.kvalitetsvurderingReference.id)
//            buildList {
//                //Saksdata
//                add(Field(fieldName = "Tilknyttet enhet", value = saksdata.tilknyttetEnhet, type = STRING))
//                add(Field(fieldName = "Sakstype", value = saksdata.sakstype.navn, type = STRING))
//                add(Field(fieldName = "Ytelse", value = saksdata.ytelse!!.navn, type = STRING))
//                add(Field(fieldName = "Mottatt vedtaksinstans", value = saksdata.mottattVedtaksinstans, type = DATE))
//                add(Field(fieldName = "Mottatt klageinstans", value = saksdata.mottattKlageinstans, type = DATE))
//                add(
//                    Field(
//                        fieldName = "Ferdigstilt",
//                        value = saksdata.avsluttetAvSaksbehandler?.toLocalDate(),
//                        type = DATE
//                    )
//                )
//                add(Field(fieldName = "Fra vedtaksenhet", value = saksdata.vedtaksinstansEnhet, type = STRING))
//                add(Field(fieldName = "Utfall/Resultat", value = saksdata.utfall!!.navn, type = STRING))
//                add(
//                    Field(
//                        fieldName = "Hjemmel",
//                        value = saksdata.registreringshjemler.toHjemlerString(),
//                        type = STRING
//                    )
//                )
//
//                //Klageforberedelsen
//                add(
//                    Field(
//                        fieldName = "Klageforberedelsen",
//                        value = kvalitetsvurderingV2.klageforberedelsenRadioValg,
//                        type = STRING
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Sakens dokumenter",
//                        value = kvalitetsvurderingV2.sakensDokumenter,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Oversittet klagefrist er ikke kommentert",
//                        value = kvalitetsvurderingV2.oversittetKlagefristIkkeKommentert,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Klagerens relevante anførseler er ikke tilstrekkelig kommentert/imøtegått",
//                        value = kvalitetsvurderingV2.klagerensRelevanteAnfoerslerIkkeKommentert,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Begrunnelse for hvorfor avslag opprettholdes / klager ikke oppfyller vilkår",
//                        value = kvalitetsvurderingV2.begrunnelseForHvorforAvslagOpprettholdes,
//                        type = BOOLEAN
//                    )
//                )
//                add(Field(fieldName = "Konklusjonen", value = kvalitetsvurderingV2.konklusjonen, type = BOOLEAN))
//                add(
//                    Field(
//                        fieldName = "Oversendelsesbrevets innhold er ikke i samsvar med sakens tema",
//                        value = kvalitetsvurderingV2.oversendelsesbrevetsInnholdIkkeISamsvarMedTema,
//                        type = BOOLEAN
//                    )
//                )
//
//                //Utredningen
//                add(Field(fieldName = "Utredningen", value = kvalitetsvurderingV2.utredningenRadioValg, type = STRING))
//                add(
//                    Field(
//                        fieldName = "Utredningen av medisinske forhold",
//                        value = kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av medisinske forhold stikkord",
//                        value = kvalitetsvurderingV2.utredningenAvMedisinskeForholdText,
//                        type = STRING
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av inntektsforhold",
//                        value = kvalitetsvurderingV2.utredningenAvInntektsforhold,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av inntektsforhold stikkord",
//                        value = kvalitetsvurderingV2.utredningenAvInntektsforholdText,
//                        type = STRING
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av arbeid",
//                        value = kvalitetsvurderingV2.utredningenAvArbeid,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av arbeid stikkord",
//                        value = kvalitetsvurderingV2.utredningenAvArbeidText,
//                        type = STRING
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Arbeidsrettet brukeroppfølging",
//                        value = kvalitetsvurderingV2.arbeidsrettetBrukeroppfoelging,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Arbeidsrettet brukeroppfølging stikkord",
//                        value = kvalitetsvurderingV2.arbeidsrettetBrukeroppfoelgingText,
//                        type = STRING
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av andre aktuelle forhold i saken",
//                        value = kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av andre aktuelle forhold i saken stikkord",
//                        value = kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISakenText,
//                        type = STRING
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av EØS / utenlandsproblematikk",
//                        value = kvalitetsvurderingV2.utredningenAvEoesProblematikk,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Utredningen av EØS / utenlandsproblematikk stikkord",
//                        value = kvalitetsvurderingV2.utredningenAvEoesProblematikkText,
//                        type = STRING
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Veiledning fra NAV",
//                        value = kvalitetsvurderingV2.veiledningFraNav,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Veiledning fra NAV stikkord",
//                        value = kvalitetsvurderingV2.veiledningFraNavText,
//                        type = STRING
//                    )
//                )
//
//                //Vedtaket
//                add(Field(fieldName = "Vedtaket", value = kvalitetsvurderingV2.vedtaketRadioValg, type = STRING))
//                add(
//                    Field(
//                        fieldName = "Det er ikke brukt riktig hjemmel(er)",
//                        value = kvalitetsvurderingV2.detErIkkeBruktRiktigHjemmel,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet",
//                        value = kvalitetsvurderingV2.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Rettsregelen er benyttet eller tolket feil",
//                        value = kvalitetsvurderingV2.rettsregelenErBenyttetFeil,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Vurdering av faktum / bevisvurdering er mangelfull",
//                        value = kvalitetsvurderingV2.vurderingAvFaktumErMangelfull,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Det er feil i den konkrete rettsanvendelsen",
//                        value = kvalitetsvurderingV2.detErFeilIKonkretRettsanvendelse,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Begrunnelsen er ikke konkret og individuell",
//                        value = kvalitetsvurderingV2.begrunnelsenErIkkeKonkretOgIndividuell,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Språket/Formidlingen er ikke tydelig",
//                        value = kvalitetsvurderingV2.spraaketErIkkeTydelig,
//                        type = BOOLEAN
//                    )
//                )
//
//                //Annet
//                add(
//                    Field(
//                        fieldName = "Nye opplysninger mottatt etter oversendelse til klageinstansen",
//                        value = kvalitetsvurderingV2.nyeOpplysningerMottatt,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Bruk gjerne vedtaket som eksempel i opplæring",
//                        value = kvalitetsvurderingV2.brukIOpplaering,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Bruk gjerne vedtaket som eksempel i opplæring stikkord",
//                        value = kvalitetsvurderingV2.brukIOpplaeringText,
//                        type = STRING
//                    )
//                )
//
//                //ROL
//                add(
//                    Field(
//                        fieldName = "Bruk av rådgivende lege",
//                        value = kvalitetsvurderingV2.brukAvRaadgivendeLegeRadioValg,
//                        type = STRING
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Rådgivende lege er ikke brukt",
//                        value = kvalitetsvurderingV2.raadgivendeLegeErIkkeBrukt,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Rådgivende lege er brukt, men saksbehandler har stilt feil spørsmål og får derfor feil svar",
//                        value = kvalitetsvurderingV2.raadgivendeLegeErBruktFeilSpoersmaal,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Rådgivende lege har uttalt seg om tema utover trygdemedisin",
//                        value = kvalitetsvurderingV2.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin,
//                        type = BOOLEAN
//                    )
//                )
//                add(
//                    Field(
//                        fieldName = "Rådgivende lege er brukt, men dokumentasjonen er mangelfull / ikke skriftliggjort",
//                        value = kvalitetsvurderingV2.raadgivendeLegeErBruktMangelfullDokumentasjon,
//                        type = BOOLEAN
//                    )
//                )
//
//                //@formatter:on
//            }
//        }
//    }

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