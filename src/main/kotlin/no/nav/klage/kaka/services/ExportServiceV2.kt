package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingV2
import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingWithoutEnheterV2
import no.nav.klage.kaka.api.view.Date
import no.nav.klage.kaka.api.view.ExcelQueryParams
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.Vedtaksinstansgruppe
import no.nav.klage.kaka.domain.kvalitetsvurdering.v2.KvalitetsvurderingV2
import no.nav.klage.kaka.domain.vedtaksinstansgruppeMap
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.repositories.SaksdataRepositoryCustomImpl
import no.nav.klage.kaka.services.ExportServiceV2.Field.Type.*
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoField
import java.util.*


@Service
class ExportServiceV2(
    private val saksdataRepository: SaksdataRepository,
) {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        private val logger = getLogger(javaClass.enclosingClass)
    }

    /**
     * Returns excel-report, for all 'finished' saksdata (anonymized (no fnr or navIdent)). For now, only used by
     * KA-ledere.
     */
    fun getAsExcel(includeFritekst: Boolean, queryParams: ExcelQueryParams): File {
        val resultList = saksdataRepository.findByQueryParamsV2(
            fromDate = queryParams.fromDate,
            toDate = queryParams.toDate,
            tilbakekreving = queryParams.tilbakekreving,
            klageenheter = queryParams.klageenheter,
            vedtaksinstansgrupper = queryParams.vedtaksinstansgrupper,
            enheter = queryParams.enheter,
            types = queryParams.types,
            ytelser = queryParams.ytelser,
            utfall = queryParams.utfall,
            hjemler = queryParams.hjemler,
        )

        val saksdataFields = mapToFields(resultList, includeFritekst)

        val workbook = SXSSFWorkbook(500)

        val sheet = workbook.createSheet("${queryParams.fromDate} til ${queryParams.toDate}")

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

        val pathToExcelFile = kotlin.io.path.createTempFile()
        val fileOutputStream = FileOutputStream(pathToExcelFile.toFile())

        workbook.write(fileOutputStream)

        fileOutputStream.close()
        workbook.dispose()

        return pathToExcelFile.toFile()
    }

    /**
     * Return all 'finished' saksdata for ledere based on given months and saksbehandlere. Cannot not be current month.
     */
    //@Cacheable(FINISHED_FOR_LEDER_CACHE)
    fun getFinishedForLederAsRawData(
        enhet: Enhet,
        fromMonth: YearMonth,
        toMonth: YearMonth,
        saksbehandlerIdentList: List<String>?
    ): AnonymizedManagerResponseV2 {
        validateNotCurrentMonth(toMonth)

        val fromDateTime = fromMonth.atDay(1).atStartOfDay()
        val toDateTime = toMonth.atEndOfMonth().atTime(LocalTime.MAX)

        val resultList = saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV2(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
        )

        return if (!saksbehandlerIdentList.isNullOrEmpty()) {
            val (mine, rest) = resultList.filter { it.saksdata.utfoerendeSaksbehandler !in saksbehandlerIdentList }
                .partition { it.saksdata.tilknyttetEnhet == enhet.navn }

            val saksbehandlerMap = saksbehandlerIdentList.associateWith { _ ->
                emptyList<AnonymizedFinishedVurderingV2>()
            }.toMutableMap()

            //Replace those who have data
            resultList.groupBy { it.saksdata.utfoerendeSaksbehandler }.forEach {
                if (saksbehandlerMap.containsKey(it.key)) {
                    saksbehandlerMap[it.key] = privateGetFinishedAsRawData(resultList = it.value.toSet())
                }
            }

            AnonymizedManagerResponseV2(
                saksbehandlere = saksbehandlerMap,
                mine = privateGetFinishedAsRawData(resultList = mine.toSet()),
                rest = privateGetFinishedAsRawData(resultList = rest.toSet()),
            )
        } else {
            val (mine, rest) = resultList.partition { it.saksdata.tilknyttetEnhet == enhet.navn }
            AnonymizedManagerResponseV2(
                saksbehandlere = emptyMap(),
                mine = privateGetFinishedAsRawData(resultList = mine.toSet()),
                rest = privateGetFinishedAsRawData(resultList = rest.toSet()),
            )
        }
    }

    private fun validateNotCurrentMonth(toMonth: YearMonth) {
        if (toMonth == YearMonth.now()) {
            throw MissingTilgangException("Cannot fetch saksdata from current month")
        }
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates
     */
    //@Cacheable(TOTAL_CACHE)
    fun getFinishedAsRawDataByDates(fromDate: LocalDate, toDate: LocalDate): List<AnonymizedFinishedVurderingV2> {
        val resultList =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV2(
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX)
            )
        return privateGetFinishedAsRawData(resultList = resultList)
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates
     */
    //@Cacheable(OPEN_CACHE)
    fun getFinishedAsRawDataByDatesWithoutEnheter(
        fromDate: LocalDate,
        toDate: LocalDate
    ): List<AnonymizedFinishedVurderingWithoutEnheterV2> {
        val resultList =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV2(
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX)
            )
        return privateGetFinishedAsRawDataWithoutEnheter(resultList = resultList)
    }

    /**
     * Return all 'finished' saksdata for vedtaksinstansleder (anonymized (no fnr or navIdent)) based on given dates
     */
    //@Cacheable(VEDTAKSINSTANSLEDER_CACHE)
    fun getFinishedAsRawDataByDatesForVedtaksinstansleder(
        fromDate: LocalDate,
        toDate: LocalDate,
        vedtaksinstansEnhet: Enhet,
        mangelfullt: List<String>,
    ): AnonymizedVedtaksinstanslederResponseV2 {
        val resultList =
            saksdataRepository.findForVedtaksinstanslederV2(
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX),
                mangelfullt = mangelfullt,
            )

        val (mine, rest) = resultList.partition { it.saksdata.vedtaksinstansEnhet == vedtaksinstansEnhet.navn }

        return AnonymizedVedtaksinstanslederResponseV2(
            mine = privateGetFinishedAsRawDataWithoutEnheterWithVersion2(resultList = mine.toSet()),
            rest = privateGetFinishedAsRawDataWithoutEnheterWithVersion2(resultList = rest.toSet()),
        )
    }

    /**
     * Return 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates and klageenhet minus given saksbehandler
     */
    //@Cacheable(FINISHED_FOR_LEDER_CACHE)
    fun getFinishedAsRawDataByDatesAndKlageenhetPartitionedBySaksbehandler(
        fromDate: LocalDate,
        toDate: LocalDate,
        saksbehandler: String,
        enhet: Enhet,
    ): AnonymizedMineRestResponseV2 {
        val resultList =
            saksdataRepository.findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV2(
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX),
                enhet = enhet.navn,
            )

        val (mine, rest) = resultList.partition { it.saksdata.utfoerendeSaksbehandler == saksbehandler }

        return AnonymizedMineRestResponseV2(
            mine = privateGetFinishedAsRawData(resultList = mine.toSet()),
            rest = privateGetFinishedAsRawData(resultList = rest.toSet()),
        )
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates
     */
    private fun privateGetFinishedAsRawData(
        resultList: Set<SaksdataRepositoryCustomImpl.QueryResultV2>,
    ): List<AnonymizedFinishedVurderingV2> {

        return resultList.map { result ->
            val (saksdata, kvalitetsvurderingV2) = result

            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

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
                vedtaksinstansgruppe = getVedtaksinstansgruppe(saksdata.vedtaksinstansEnhet!!).id,
                mottattKlageinstans = mottattKlageinstansDate,
                tilbakekreving = saksdata.tilbakekreving,

                klageforberedelsenSakensDokumenter = kvalitetsvurderingV2.klageforberedelsenSakensDokumenter,
                klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
                klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
                klageforberedelsen = kvalitetsvurderingV2.klageforberedelsen?.name,
                klageforberedelsenOversittetKlagefristIkkeKommentert = kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
                klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
                klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                klageforberedelsenUtredningenUnderKlageforberedelsen = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsen,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
                utredningen = kvalitetsvurderingV2.utredningen?.name,
                utredningenAvMedisinskeForhold = kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                utredningenAvInntektsforhold = kvalitetsvurderingV2.utredningenAvInntektsforhold,
                utredningenAvArbeidsaktivitet = kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                utredningenAvEoesUtenlandsproblematikk = kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                utredningenAvAndreAktuelleForholdISaken = kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvSivilstandBoforhold = kvalitetsvurderingV2.utredningenAvSivilstandBoforhold,
                vedtaketLovbestemmelsenTolketFeil = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                vedtaketLovbestemmelsenTolketFeilHjemlerList = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmel = kvalitetsvurderingV2.vedtaketBruktFeilHjemmel,
                vedtaketBruktFeilHjemmelHjemlerList = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelHjemlerList?.map { it.id },
                vedtaketAlleRelevanteHjemlerErIkkeVurdert = kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList = kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketFeilKonkretRettsanvendelse = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                vedtaketFeilKonkretRettsanvendelseHjemlerList = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id },
                vedtaketIkkeKonkretIndividuellBegrunnelse = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
                vedtaketAutomatiskVedtak = kvalitetsvurderingV2.vedtaketAutomatiskVedtak,
                vedtaket = kvalitetsvurderingV2.vedtaket?.name,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList?.map { it.id },
                vedtaketDetErLagtTilGrunnFeilFaktum = kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                vedtaketSpraakOgFormidlingErIkkeTydelig = kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                raadgivendeLegeIkkebrukt = kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                brukAvRaadgivendeLege = kvalitetsvurderingV2.brukAvRaadgivendeLege?.name,

                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata, kvalitetsvurderingV2),
                modifiedDate = getModifiedDate(saksdata, kvalitetsvurderingV2),

                )
        }
    }

    private fun getVedtaksinstansgruppe(vedtaksinstansEnhet: String): Vedtaksinstansgruppe {
        val vedtaksinstansgruppe = vedtaksinstansgruppeMap[vedtaksinstansEnhet.take(2)]
        return if (vedtaksinstansgruppe != null) {
            vedtaksinstansgruppe
        } else {
            if (vedtaksinstansEnhet == "9999") {
                logger.debug(
                    "Ukjent enhet. Kan ikke mappe til vedtaksinstansgruppe. vedtaksinstansEnhet: {}",
                    vedtaksinstansEnhet
                )
            } else {
                logger.warn(
                    "Ukjent enhet. Kan ikke mappe til vedtaksinstansgruppe. vedtaksinstansEnhet: {}",
                    vedtaksinstansEnhet
                )
            }
            Vedtaksinstansgruppe.UNKNOWN
        }
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr, navIdent or enheter)) based on given dates.
     */
    private fun privateGetFinishedAsRawDataWithoutEnheter(
        resultList: Set<SaksdataRepositoryCustomImpl.QueryResultV2>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV2> {

        return resultList.map { result ->
            val (saksdata, kvalitetsvurderingV2) = result

            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay
            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            AnonymizedFinishedVurderingWithoutEnheterV2(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                hjemmelIdList = saksdata.registreringshjemler!!.map { it.id },
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandlerDate,
                ytelseId = saksdata.ytelse!!.id,
                utfallId = saksdata.utfall!!.id,
                sakstypeId = saksdata.sakstype.id,
                mottattVedtaksinstans = saksdata.mottattVedtaksinstans?.toDate(),
                mottattKlageinstans = mottattKlageinstansDate,
                tilbakekreving = saksdata.tilbakekreving,

                klageforberedelsenSakensDokumenter = kvalitetsvurderingV2.klageforberedelsenSakensDokumenter,
                klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
                klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
                klageforberedelsen = kvalitetsvurderingV2.klageforberedelsen?.name,
                klageforberedelsenOversittetKlagefristIkkeKommentert = kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
                klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
                klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                klageforberedelsenUtredningenUnderKlageforberedelsen = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsen,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
                utredningen = kvalitetsvurderingV2.utredningen?.name,
                utredningenAvMedisinskeForhold = kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                utredningenAvInntektsforhold = kvalitetsvurderingV2.utredningenAvInntektsforhold,
                utredningenAvArbeidsaktivitet = kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                utredningenAvEoesUtenlandsproblematikk = kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                utredningenAvAndreAktuelleForholdISaken = kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvSivilstandBoforhold = kvalitetsvurderingV2.utredningenAvSivilstandBoforhold,
                vedtaketLovbestemmelsenTolketFeil = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                vedtaketLovbestemmelsenTolketFeilHjemlerList = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmel = kvalitetsvurderingV2.vedtaketBruktFeilHjemmel,
                vedtaketBruktFeilHjemmelHjemlerList = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelHjemlerList?.map { it.id },
                vedtaketAlleRelevanteHjemlerErIkkeVurdert = kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList = kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketFeilKonkretRettsanvendelse = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                vedtaketFeilKonkretRettsanvendelseHjemlerList = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id },
                vedtaketIkkeKonkretIndividuellBegrunnelse = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
                vedtaketAutomatiskVedtak = kvalitetsvurderingV2.vedtaketAutomatiskVedtak,
                vedtaket = kvalitetsvurderingV2.vedtaket?.name,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList?.map { it.id },
                vedtaketDetErLagtTilGrunnFeilFaktum = kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                vedtaketSpraakOgFormidlingErIkkeTydelig = kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                raadgivendeLegeIkkebrukt = kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                brukAvRaadgivendeLege = kvalitetsvurderingV2.brukAvRaadgivendeLege?.name,

                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata, kvalitetsvurderingV2),
                modifiedDate = getModifiedDate(saksdata, kvalitetsvurderingV2),
            )
        }
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr, navIdent or enheter)) based on given dates.
     */
    private fun privateGetFinishedAsRawDataWithoutEnheterWithVersion2(
        resultList: Set<SaksdataRepositoryCustomImpl.QueryResultV2>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV2> {

        return resultList.map { result ->
            val (saksdata, kvalitetsvurderingV2) = result

            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay
            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            if (saksdata.kvalitetsvurderingReference.version == 1) {
                error("This query only works for version 2 of kvalitetsvurderinger")
            }

            AnonymizedFinishedVurderingWithoutEnheterV2(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                hjemmelIdList = saksdata.registreringshjemler!!.map { it.id },
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandlerDate,
                ytelseId = saksdata.ytelse!!.id,
                utfallId = saksdata.utfall!!.id,
                sakstypeId = saksdata.sakstype.id,
                mottattVedtaksinstans = saksdata.mottattVedtaksinstans?.toDate(),
                mottattKlageinstans = mottattKlageinstansDate,
                tilbakekreving = saksdata.tilbakekreving,

                klageforberedelsenSakensDokumenter = kvalitetsvurderingV2.klageforberedelsenSakensDokumenter,
                klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
                klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
                klageforberedelsen = kvalitetsvurderingV2.klageforberedelsen?.name,
                klageforberedelsenOversittetKlagefristIkkeKommentert = kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
                klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
                klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                klageforberedelsenUtredningenUnderKlageforberedelsen = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsen,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
                utredningen = kvalitetsvurderingV2.utredningen?.name,
                utredningenAvMedisinskeForhold = kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                utredningenAvInntektsforhold = kvalitetsvurderingV2.utredningenAvInntektsforhold,
                utredningenAvArbeidsaktivitet = kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                utredningenAvEoesUtenlandsproblematikk = kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                utredningenAvAndreAktuelleForholdISaken = kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvSivilstandBoforhold = kvalitetsvurderingV2.utredningenAvSivilstandBoforhold,
                vedtaketLovbestemmelsenTolketFeil = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                vedtaketLovbestemmelsenTolketFeilHjemlerList = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmel = kvalitetsvurderingV2.vedtaketBruktFeilHjemmel,
                vedtaketBruktFeilHjemmelHjemlerList = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelHjemlerList?.map { it.id },
                vedtaketAlleRelevanteHjemlerErIkkeVurdert = kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList = kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketFeilKonkretRettsanvendelse = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                vedtaketFeilKonkretRettsanvendelseHjemlerList = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id },
                vedtaketIkkeKonkretIndividuellBegrunnelse = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
                vedtaketAutomatiskVedtak = kvalitetsvurderingV2.vedtaketAutomatiskVedtak,
                vedtaket = kvalitetsvurderingV2.vedtaket?.name,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList?.map { it.id },
                vedtaketDetErLagtTilGrunnFeilFaktum = kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                vedtaketSpraakOgFormidlingErIkkeTydelig = kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                raadgivendeLegeIkkebrukt = kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                brukAvRaadgivendeLege = kvalitetsvurderingV2.brukAvRaadgivendeLege?.name,

                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata, kvalitetsvurderingV2),
                modifiedDate = getModifiedDate(saksdata, kvalitetsvurderingV2),
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
        val mottattForrigeInstans = if (saksdata.sakstype in listOf(Type.ANKE, Type.BEHANDLING_ETTER_TRYGDERETTEN_OPPHEVET, Type.OMGJOERINGSKRAV)) {
            saksdata.mottattKlageinstans!!.toDate()
        } else {
            saksdata.mottattVedtaksinstans!!.toDate()
        }
        return mottattForrigeInstans
    }

    private fun getCreatedDate(saksdata: Saksdata, kvalitetsvurderingV2: KvalitetsvurderingV2? = null): Date {
        if (kvalitetsvurderingV2 == null) {
            return saksdata.created.toDate()
        }

        return if (saksdata.created.isBefore(kvalitetsvurderingV2.created)) {
            saksdata.created.toDate()
        } else {
            kvalitetsvurderingV2.created.toDate()
        }
    }

    private fun getModifiedDate(saksdata: Saksdata, kvalitetsvurderingV2: KvalitetsvurderingV2? = null): Date {
        if (kvalitetsvurderingV2 == null) {
            return saksdata.created.toDate()
        }

        return if (saksdata.modified.isAfter(kvalitetsvurderingV2.modified)) {
            saksdata.modified.toDate()
        } else {
            kvalitetsvurderingV2.modified.toDate()
        }
    }

    private fun mapToFields(
        saksdataList: Set<SaksdataRepositoryCustomImpl.QueryResultV2>,
        includeFritekst: Boolean
    ): List<List<Field>> {
        //@formatter:off
        return saksdataList.map { result ->
            val (saksdata, kvalitetsvurderingV2) = result
            if (saksdata.kvalitetsvurderingReference.version == 1) {
                error("This query only works for version 2 of kvalitetsvurderinger")
            }

            buildList {
                //Saksdata
                add(Field(fieldName = "Tilknyttet enhet", value = saksdata.tilknyttetEnhet, type = STRING))
                add(Field(fieldName = "Sakstype", value = saksdata.sakstype.navn, type = STRING))
                add(Field(fieldName = "Ytelse", value = saksdata.ytelse!!.navn, type = STRING))
                add(Field(fieldName = "Mottatt vedtaksinstans", value = saksdata.mottattVedtaksinstans, type = DATE))
                add(Field(fieldName = "Mottatt klageinstans", value = saksdata.mottattKlageinstans, type = DATE))
                add(
                    Field(
                        fieldName = "Ferdigstilt",
                        value = saksdata.avsluttetAvSaksbehandler?.toLocalDate(),
                        type = DATE
                    )
                )
                add(Field(fieldName = "Fra vedtaksenhet", value = saksdata.vedtaksinstansEnhet, type = STRING))
                add(Field(fieldName = "Utfall/Resultat", value = saksdata.utfall!!.navn, type = STRING))
                add(
                    Field(
                        fieldName = "Hjemmel",
                        value = saksdata.registreringshjemler.toHjemlerString(),
                        type = STRING
                    )
                )
                add(Field(fieldName = "Tilbakekreving", value = saksdata.tilbakekreving, type = BOOLEAN))

                //Klageforberedelsen
                add(
                    Field(
                        fieldName = "Klageforberedelsen",
                        value = kvalitetsvurderingV2.klageforberedelsen,
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Sakens dokumenter",
                        value = kvalitetsvurderingV2.klageforberedelsenSakensDokumenter,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Relevante opplysninger fra andre fagsystemer er ikke journalført",
                        value = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Journalførte dokumenter har feil titler/navn",
                        value = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Mangler fysisk saksmappe",
                        value = kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen under klageforberedelsen",
                        value = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsen,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Klageinstansen har bedt underinstansen om å innhente nye opplysninger",
                        value = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
                        type = BOOLEAN
                    )
                )
                if (includeFritekst) {
                    add(
                        Field(
                            fieldName = "Skriv hvilke opplysninger som måtte hentes inn her (valgfri)",
                            value = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysningerFritekst,
                            type = STRING
                        )
                    )
                }
                add(
                    Field(
                        fieldName = "Klageinstansen har selv innhentet nye opplysninger",
                        value = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
                        type = BOOLEAN
                    )
                )
                if (includeFritekst) {
                    add(
                        Field(
                            fieldName = "Skriv hvilke opplysninger som måtte hentes inn her (valgfri)",
                            value = kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysningerFritekst,
                            type = STRING
                        )
                    )
                }
                add(
                    Field(
                        fieldName = "Oversittet klagefrist er ikke kommentert",
                        value = kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Klagers relevante anførsler er ikke tilstrekkelig kommentert/imøtegått",
                        value = kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Feil ved begrunnelsen for hvorfor avslag opprettholdes/klager ikke oppfyller vilkår",
                        value = kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Oversendelsesbrevets innhold er ikke i samsvar med sakens tema",
                        value = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er ikke sendt kopi av oversendelsesbrevet til parten, eller det er sendt til feil mottaker",
                        value = kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                        type = BOOLEAN
                    )
                )

                //Utredningen
                add(Field(fieldName = "Utredningen", value = kvalitetsvurderingV2.utredningen, type = STRING))
                add(
                    Field(
                        fieldName = "Utredningen av medisinske forhold",
                        value = kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av inntektsforhold",
                        value = kvalitetsvurderingV2.utredningenAvInntektsforhold,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av arbeidsaktivitet",
                        value = kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av EØS-/utenlandsproblematikk",
                        value = kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av sivilstand/boforhold",
                        value = kvalitetsvurderingV2.utredningenAvSivilstandBoforhold,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av andre aktuelle forhold i saken",
                        value = kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                        type = BOOLEAN
                    )
                )

                //Automatisk vedtak
                add(
                    Field(
                        fieldName = "Avhuking for automatiske vedtak",
                        value = kvalitetsvurderingV2.vedtaketAutomatiskVedtak,
                        type = BOOLEAN
                    )
                )

                //Vedtaket
                add(Field(fieldName = "Vedtaket", value = kvalitetsvurderingV2.vedtaket, type = STRING))
                add(
                    Field(
                        fieldName = "Det er brukt feil hjemmel eller alle relevante hjemler er ikke vurdert",
                        value = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er brukt feil hjemmel eller alle relevante hjemler er ikke vurdert - hjemler",
                        value = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Det er brukt feil hjemmel",
                        value = kvalitetsvurderingV2.vedtaketBruktFeilHjemmel,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Hjemler for «Det er brukt feil hjemmel»",
                        value = kvalitetsvurderingV2.vedtaketBruktFeilHjemmelHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Alle relevante hjemler er ikke vurdert",
                        value = kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Hjemler for «Alle relevante hjemler er ikke vurdert»",
                        value = kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Lovbestemmelsen er tolket feil",
                        value = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Lovbestemmelsen er tolket feil - hjemler",
                        value = kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )

                add(
                    Field(
                        fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet",
                        value = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet - hjemale",
                        value = kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )

                add(
                    Field(
                        fieldName = "Det er lagt til grunn feil faktum",
                        value = kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                        type = BOOLEAN
                    )
                )

                add(
                    Field(
                        fieldName = "Feil i den konkrete rettsanvendelsen",
                        value = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Feil i den konkrete rettsanvendelsen - hjemler",
                        value = kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )

                add(
                    Field(
                        fieldName = "Begrunnelsen er ikke konkret og individuell nok",
                        value = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det går ikke godt nok frem hva slags faktum som er lagt til grunn",
                        value = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det går ikke godt nok frem hvordan rettsregelen er anvendt på faktum",
                        value = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er mye standardtekst",
                        value = kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
                        type = BOOLEAN
                    )
                )

                add(
                    Field(
                        fieldName = "Språket og formidlingen er ikke tydelig",
                        value = kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                        type = BOOLEAN
                    )
                )

                //ROL
                add(
                    Field(
                        fieldName = "Bruk av rådgivende lege",
                        value = kvalitetsvurderingV2.brukAvRaadgivendeLege,
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege er ikke brukt",
                        value = kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Saksbehandlers bruk av rådgivende lege er mangelfull",
                        value = kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege har uttalt seg om tema utover trygdemedisin",
                        value = kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege er brukt, men begrunnelsen fra rådgivende lege er mangelfull eller ikke dokumentert",
                        value = kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                        type = BOOLEAN
                    )
                )

                //Annet
                if (includeFritekst) {
                    add(
                        Field(
                            fieldName = "Annet",
                            value = kvalitetsvurderingV2.annetFritekst,
                            type = STRING
                        )
                    )
                }
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

data class AnonymizedMineRestResponseV2(
    val mine: List<AnonymizedFinishedVurderingV2>,
    val rest: List<AnonymizedFinishedVurderingV2>,
)

data class AnonymizedManagerResponseV2(
    val saksbehandlere: Map<String, List<AnonymizedFinishedVurderingV2>>,
    val mine: List<AnonymizedFinishedVurderingV2>,
    val rest: List<AnonymizedFinishedVurderingV2>
)

data class AnonymizedVedtaksinstanslederResponseV2(
    val mine: List<AnonymizedFinishedVurderingWithoutEnheterV2>,
    val rest: List<AnonymizedFinishedVurderingWithoutEnheterV2>,
)