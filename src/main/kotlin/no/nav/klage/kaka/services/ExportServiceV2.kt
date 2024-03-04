package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingV2
import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingWithoutEnheterV2
import no.nav.klage.kaka.api.view.Date
import no.nav.klage.kaka.api.view.Vedtaksinstansgruppe
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.services.ExportServiceV2.Field.Type.*
import no.nav.klage.kaka.util.getLogger
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.Type
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.time.*
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
    fun getAsExcel(year: Year, includeFritekst: Boolean): File {
        val resultList = saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV2(
            fromDateTime = LocalDate.of(year.value, Month.JANUARY, 1).atStartOfDay(),
            toDateTime = LocalDate.of(year.value, Month.DECEMBER, 31).atTime(LocalTime.MAX),
        )

        val saksdataFields = mapToFields(resultList, includeFritekst)

        val workbook = SXSSFWorkbook(500)

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
            val (mine, rest) = resultList.filter { it.utfoerendeSaksbehandler !in saksbehandlerIdentList }
                .partition { it.tilknyttetEnhet == enhet.navn }

            val saksbehandlerMap = saksbehandlerIdentList.associateWith { _ ->
                emptyList<AnonymizedFinishedVurderingV2>()
            }.toMutableMap()

            //Replace those who have data
            resultList.groupBy { it.utfoerendeSaksbehandler }.forEach {
                if (saksbehandlerMap.containsKey(it.key)) {
                    saksbehandlerMap[it.key] = privateGetFinishedAsRawData(resultList = it.value)
                }
            }

            AnonymizedManagerResponseV2(
                saksbehandlere = saksbehandlerMap,
                mine = privateGetFinishedAsRawData(resultList = mine),
                rest = privateGetFinishedAsRawData(resultList = rest),
            )
        } else {
            val (mine, rest) = resultList.partition { it.tilknyttetEnhet == enhet.navn }
            AnonymizedManagerResponseV2(
                saksbehandlere = emptyMap(),
                mine = privateGetFinishedAsRawData(resultList = mine),
                rest = privateGetFinishedAsRawData(resultList = rest),
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

        val (mine, rest) = resultList.partition { it.vedtaksinstansEnhet == vedtaksinstansEnhet.navn }

        return AnonymizedVedtaksinstanslederResponseV2(
            mine = privateGetFinishedAsRawDataWithoutEnheterWithVersion2(resultList = mine),
            rest = privateGetFinishedAsRawDataWithoutEnheterWithVersion2(resultList = rest),
        )
    }

    /**
     * Return 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates and klageenhet minus given saksbehandler
     */
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

        val (mine, rest) = resultList.partition { it.utfoerendeSaksbehandler == saksbehandler }

        return AnonymizedMineRestResponseV2(
            mine = privateGetFinishedAsRawData(resultList = mine),
            rest = privateGetFinishedAsRawData(resultList = rest),
        )
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates
     */
    private fun privateGetFinishedAsRawData(
        resultList: List<Saksdata>,
    ): List<AnonymizedFinishedVurderingV2> {
        val start = System.currentTimeMillis()

        val map = resultList.map { saksdata ->

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

                klageforberedelsenSakensDokumenter = saksdata.kvalitetsvurderingV2!!.klageforberedelsenSakensDokumenter,
                klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
                klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
                klageforberedelsen = saksdata.kvalitetsvurderingV2.klageforberedelsen?.name,
                klageforberedelsenOversittetKlagefristIkkeKommentert = saksdata.kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = saksdata.kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
                klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = saksdata.kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
                klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = saksdata.kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = saksdata.kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                klageforberedelsenUtredningenUnderKlageforberedelsen = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsen,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
                utredningen = saksdata.kvalitetsvurderingV2.utredningen?.name,
                utredningenAvMedisinskeForhold = saksdata.kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                utredningenAvInntektsforhold = saksdata.kvalitetsvurderingV2.utredningenAvInntektsforhold,
                utredningenAvArbeidsaktivitet = saksdata.kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                utredningenAvEoesUtenlandsproblematikk = saksdata.kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                utredningenAvAndreAktuelleForholdISaken = saksdata.kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvSivilstandBoforhold = saksdata.kvalitetsvurderingV2.utredningenAvSivilstandBoforhold,
                vedtaketLovbestemmelsenTolketFeil = saksdata.kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                vedtaketLovbestemmelsenTolketFeilHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmel = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmel,
                vedtaketBruktFeilHjemmelHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelHjemlerList?.map { it.id },
                vedtaketAlleRelevanteHjemlerErIkkeVurdert = saksdata.kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketFeilKonkretRettsanvendelse = saksdata.kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                vedtaketFeilKonkretRettsanvendelseHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id },
                vedtaketIkkeKonkretIndividuellBegrunnelse = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
                vedtaketAutomatiskVedtak = saksdata.kvalitetsvurderingV2.vedtaketAutomatiskVedtak,
                vedtaket = saksdata.kvalitetsvurderingV2.vedtaket?.name,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = saksdata.kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList?.map { it.id },
                vedtaketDetErLagtTilGrunnFeilFaktum = saksdata.kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                vedtaketSpraakOgFormidlingErIkkeTydelig = saksdata.kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                raadgivendeLegeIkkebrukt = saksdata.kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = saksdata.kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = saksdata.kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = saksdata.kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                brukAvRaadgivendeLege = saksdata.kvalitetsvurderingV2.brukAvRaadgivendeLege?.name,

                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata),
                modifiedDate = getModifiedDate(saksdata),

                )
        }
        logger.debug("After map operation: ${System.currentTimeMillis() - start} millis")
        return map
    }

    private fun getVedtaksinstansgruppe(vedtaksinstansEnhet: String): Vedtaksinstansgruppe {
        return when (vedtaksinstansEnhet.take(2)) {
            "02" -> Vedtaksinstansgruppe.AKERSHUS
            "03" -> Vedtaksinstansgruppe.OSLO
            "46", "12", "14", "13" -> Vedtaksinstansgruppe.VESTLAND
            "11" -> Vedtaksinstansgruppe.ROGALAND
            "50", "16", "17", "57" -> Vedtaksinstansgruppe.TROENDELAG
            "34", "04", "05" -> Vedtaksinstansgruppe.INNLANDET
            "09", "10" -> Vedtaksinstansgruppe.AGDER
            "01" -> Vedtaksinstansgruppe.OESTFOLD
            "15" -> Vedtaksinstansgruppe.MOERE_OG_ROMSDAL
            "06" -> Vedtaksinstansgruppe.BUSKERUD
            "07", "53" -> Vedtaksinstansgruppe.VESTFOLD
            "18" -> Vedtaksinstansgruppe.NORDLAND
            "08" -> Vedtaksinstansgruppe.TELEMARK
            "19" -> Vedtaksinstansgruppe.TROMS
            "20" -> Vedtaksinstansgruppe.FINNMARK
            "41" -> Vedtaksinstansgruppe.NAV_OEKONOMI_STOENAD
            "44" -> Vedtaksinstansgruppe.NAV_ARBEID_OG_YTELSER
            "45" -> Vedtaksinstansgruppe.NAV_KONTROLL_FORVALTNING
            "47" -> Vedtaksinstansgruppe.NAV_HJELPEMIDDELSENTRAL
            "48" -> Vedtaksinstansgruppe.NAV_FAMILIE_OG_PENSJONSYTELSER
            "42", "00" -> Vedtaksinstansgruppe.UNKNOWN // 42: Klageenhet, expected for anke. 00: Utland
            else -> {
                logger.warn(
                    "Ukjent enhet. Kan ikke mappe til vedtaksinstansgruppe. vedtaksinstansEnhet: {}",
                    vedtaksinstansEnhet
                )
                Vedtaksinstansgruppe.UNKNOWN
            }
        }
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr, navIdent or enheter)) based on given dates.
     */
    private fun privateGetFinishedAsRawDataWithoutEnheter(
        resultList: List<Saksdata>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV2> {

        return resultList.map { saksdata ->

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

                klageforberedelsenSakensDokumenter = saksdata.kvalitetsvurderingV2!!.klageforberedelsenSakensDokumenter,
                klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
                klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
                klageforberedelsen = saksdata.kvalitetsvurderingV2.klageforberedelsen?.name,
                klageforberedelsenOversittetKlagefristIkkeKommentert = saksdata.kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = saksdata.kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
                klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = saksdata.kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
                klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = saksdata.kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = saksdata.kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                klageforberedelsenUtredningenUnderKlageforberedelsen = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsen,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
                utredningen = saksdata.kvalitetsvurderingV2.utredningen?.name,
                utredningenAvMedisinskeForhold = saksdata.kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                utredningenAvInntektsforhold = saksdata.kvalitetsvurderingV2.utredningenAvInntektsforhold,
                utredningenAvArbeidsaktivitet = saksdata.kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                utredningenAvEoesUtenlandsproblematikk = saksdata.kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                utredningenAvAndreAktuelleForholdISaken = saksdata.kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvSivilstandBoforhold = saksdata.kvalitetsvurderingV2.utredningenAvSivilstandBoforhold,
                vedtaketLovbestemmelsenTolketFeil = saksdata.kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                vedtaketLovbestemmelsenTolketFeilHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmel = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmel,
                vedtaketBruktFeilHjemmelHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelHjemlerList?.map { it.id },
                vedtaketAlleRelevanteHjemlerErIkkeVurdert = saksdata.kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketFeilKonkretRettsanvendelse = saksdata.kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                vedtaketFeilKonkretRettsanvendelseHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id },
                vedtaketIkkeKonkretIndividuellBegrunnelse = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
                vedtaketAutomatiskVedtak = saksdata.kvalitetsvurderingV2.vedtaketAutomatiskVedtak,
                vedtaket = saksdata.kvalitetsvurderingV2.vedtaket?.name,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = saksdata.kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList?.map { it.id },
                vedtaketDetErLagtTilGrunnFeilFaktum = saksdata.kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                vedtaketSpraakOgFormidlingErIkkeTydelig = saksdata.kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                raadgivendeLegeIkkebrukt = saksdata.kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = saksdata.kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = saksdata.kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = saksdata.kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                brukAvRaadgivendeLege = saksdata.kvalitetsvurderingV2.brukAvRaadgivendeLege?.name,

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
    private fun privateGetFinishedAsRawDataWithoutEnheterWithVersion2(
        resultList: List<Saksdata>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV2> {

        return resultList.map { saksdata ->

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

                klageforberedelsenSakensDokumenter = saksdata.kvalitetsvurderingV2!!.klageforberedelsenSakensDokumenter,
                klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
                klageforberedelsenSakensDokumenterManglerFysiskSaksmappe = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
                klageforberedelsen = saksdata.kvalitetsvurderingV2.klageforberedelsen?.name,
                klageforberedelsenOversittetKlagefristIkkeKommentert = saksdata.kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt = saksdata.kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
                klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar = saksdata.kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
                klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema = saksdata.kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker = saksdata.kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                klageforberedelsenUtredningenUnderKlageforberedelsen = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsen,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
                klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
                utredningen = saksdata.kvalitetsvurderingV2.utredningen?.name,
                utredningenAvMedisinskeForhold = saksdata.kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                utredningenAvInntektsforhold = saksdata.kvalitetsvurderingV2.utredningenAvInntektsforhold,
                utredningenAvArbeidsaktivitet = saksdata.kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                utredningenAvEoesUtenlandsproblematikk = saksdata.kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                utredningenAvAndreAktuelleForholdISaken = saksdata.kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                utredningenAvSivilstandBoforhold = saksdata.kvalitetsvurderingV2.utredningenAvSivilstandBoforhold,
                vedtaketLovbestemmelsenTolketFeil = saksdata.kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                vedtaketLovbestemmelsenTolketFeilHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketBruktFeilHjemmel = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmel,
                vedtaketBruktFeilHjemmelHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelHjemlerList?.map { it.id },
                vedtaketAlleRelevanteHjemlerErIkkeVurdert = saksdata.kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdert,
                vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList?.map { it.id },
                vedtaketFeilKonkretRettsanvendelse = saksdata.kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                vedtaketFeilKonkretRettsanvendelseHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList?.map { it.id },
                vedtaketIkkeKonkretIndividuellBegrunnelse = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
                vedtaketAutomatiskVedtak = saksdata.kvalitetsvurderingV2.vedtaketAutomatiskVedtak,
                vedtaket = saksdata.kvalitetsvurderingV2.vedtaket?.name,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet = saksdata.kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList = saksdata.kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList?.map { it.id },
                vedtaketDetErLagtTilGrunnFeilFaktum = saksdata.kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                vedtaketSpraakOgFormidlingErIkkeTydelig = saksdata.kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                raadgivendeLegeIkkebrukt = saksdata.kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = saksdata.kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = saksdata.kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = saksdata.kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                brukAvRaadgivendeLege = saksdata.kvalitetsvurderingV2.brukAvRaadgivendeLege?.name,

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
        if (saksdata.kvalitetsvurderingV2 == null) {
            return saksdata.created.toDate()
        }

        return if (saksdata.created.isBefore(saksdata.kvalitetsvurderingV2.created)) {
            saksdata.created.toDate()
        } else {
            saksdata.kvalitetsvurderingV2.created.toDate()
        }
    }

    private fun getModifiedDate(saksdata: Saksdata): Date {
        if (saksdata.kvalitetsvurderingV2 == null) {
            return saksdata.created.toDate()
        }

        return if (saksdata.modified.isAfter(saksdata.kvalitetsvurderingV2.modified)) {
            saksdata.modified.toDate()
        } else {
            saksdata.kvalitetsvurderingV2.modified.toDate()
        }
    }

    private fun mapToFields(saksdataList: List<Saksdata>, includeFritekst: Boolean): List<List<Field>> {
        //@formatter:off
        return saksdataList.map { saksdata ->
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

                //Klageforberedelsen
                add(
                    Field(
                        fieldName = "Klageforberedelsen",
                        value = saksdata.kvalitetsvurderingV2!!.klageforberedelsen,
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Sakens dokumenter",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenter,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Relevante opplysninger fra andre fagsystemer er ikke journalført",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterRelevanteOpplysningerFraAndreFagsystemerErIkkeJournalfoert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Journalførte dokumenter har feil titler/navn",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterJournalfoerteDokumenterFeilNavn,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Mangler fysisk saksmappe",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenSakensDokumenterManglerFysiskSaksmappe,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen under klageforberedelsen",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsen,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Klageinstansen har bedt underinstansen om å innhente nye opplysninger",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysninger,
                        type = BOOLEAN
                    )
                )
                if (includeFritekst) {
                    add(
                        Field(
                            fieldName = "Skriv hvilke opplysninger som måtte hentes inn her (valgfri)",
                            value = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarBedtUnderinstansenOmAaInnhenteNyeOpplysningerFritekst,
                            type = STRING
                        )
                    )
                }
                add(
                    Field(
                        fieldName = "Klageinstansen har selv innhentet nye opplysninger",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysninger,
                        type = BOOLEAN
                    )
                )
                if (includeFritekst) {
                    add(
                        Field(
                            fieldName = "Skriv hvilke opplysninger som måtte hentes inn her (valgfri)",
                            value = saksdata.kvalitetsvurderingV2.klageforberedelsenUtredningenUnderKlageforberedelsenKlageinstansenHarSelvInnhentetNyeOpplysningerFritekst,
                            type = STRING
                        )
                    )
                }
                add(
                    Field(
                        fieldName = "Oversittet klagefrist er ikke kommentert",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenOversittetKlagefristIkkeKommentert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Klagers relevante anførsler er ikke tilstrekkelig kommentert/imøtegått",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenKlagersRelevanteAnfoerslerIkkeTilstrekkeligKommentertImoetegaatt,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Feil ved begrunnelsen for hvorfor avslag opprettholdes/klager ikke oppfyller vilkår",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenFeilVedBegrunnelsenForHvorforAvslagOpprettholdesKlagerIkkeOppfyllerVilkaar,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Oversendelsesbrevets innhold er ikke i samsvar med sakens tema",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevetsInnholdErIkkeISamsvarMedSakensTema,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er ikke sendt kopi av oversendelsesbrevet til parten, eller det er sendt til feil mottaker",
                        value = saksdata.kvalitetsvurderingV2.klageforberedelsenOversendelsesbrevIkkeSendtKopiTilPartenEllerFeilMottaker,
                        type = BOOLEAN
                    )
                )

                //Utredningen
                add(Field(fieldName = "Utredningen", value = saksdata.kvalitetsvurderingV2.utredningen, type = STRING))
                add(
                    Field(
                        fieldName = "Utredningen av medisinske forhold",
                        value = saksdata.kvalitetsvurderingV2.utredningenAvMedisinskeForhold,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av inntektsforhold",
                        value = saksdata.kvalitetsvurderingV2.utredningenAvInntektsforhold,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av arbeidsaktivitet",
                        value = saksdata.kvalitetsvurderingV2.utredningenAvArbeidsaktivitet,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av EØS-/utenlandsproblematikk",
                        value = saksdata.kvalitetsvurderingV2.utredningenAvEoesUtenlandsproblematikk,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av sivilstand/boforhold",
                        value = saksdata.kvalitetsvurderingV2.utredningenAvSivilstandBoforhold,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av andre aktuelle forhold i saken",
                        value = saksdata.kvalitetsvurderingV2.utredningenAvAndreAktuelleForholdISaken,
                        type = BOOLEAN
                    )
                )

                //Automatisk vedtak
                add(
                    Field(
                        fieldName = "Avhuking for automatiske vedtak",
                        value = saksdata.kvalitetsvurderingV2.vedtaketAutomatiskVedtak,
                        type = BOOLEAN
                    )
                )

                //Vedtaket
                add(Field(fieldName = "Vedtaket", value = saksdata.kvalitetsvurderingV2.vedtaket, type = STRING))
                add(
                    Field(
                        fieldName = "Det er brukt feil hjemmel eller alle relevante hjemler er ikke vurdert",
                        value = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er brukt feil hjemmel eller alle relevante hjemler er ikke vurdert - hjemler",
                        value = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelEllerAlleRelevanteHjemlerErIkkeVurdertHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Det er brukt feil hjemmel",
                        value = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmel,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Hjemler for «Det er brukt feil hjemmel»",
                        value = saksdata.kvalitetsvurderingV2.vedtaketBruktFeilHjemmelHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Alle relevante hjemler er ikke vurdert",
                        value = saksdata.kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Hjemler for «Alle relevante hjemler er ikke vurdert»",
                        value = saksdata.kvalitetsvurderingV2.vedtaketAlleRelevanteHjemlerErIkkeVurdertHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Lovbestemmelsen er tolket feil",
                        value = saksdata.kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeil,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Lovbestemmelsen er tolket feil - hjemler",
                        value = saksdata.kvalitetsvurderingV2.vedtaketLovbestemmelsenTolketFeilHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )

                add(
                    Field(
                        fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet",
                        value = saksdata.kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevet,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet - hjemale",
                        value = saksdata.kvalitetsvurderingV2.vedtaketInnholdetIRettsregleneErIkkeTilstrekkeligBeskrevetHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )

                add(
                    Field(
                        fieldName = "Det er lagt til grunn feil faktum",
                        value = saksdata.kvalitetsvurderingV2.vedtaketDetErLagtTilGrunnFeilFaktum,
                        type = BOOLEAN
                    )
                )

                add(
                    Field(
                        fieldName = "Feil i den konkrete rettsanvendelsen",
                        value = saksdata.kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelse,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Feil i den konkrete rettsanvendelsen - hjemler",
                        value = saksdata.kvalitetsvurderingV2.vedtaketFeilKonkretRettsanvendelseHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )

                add(
                    Field(
                        fieldName = "Begrunnelsen er ikke konkret og individuell nok",
                        value = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelse,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det går ikke godt nok frem hva slags faktum som er lagt til grunn",
                        value = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremFaktum,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det går ikke godt nok frem hvordan rettsregelen er anvendt på faktum",
                        value = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseIkkeGodtNokFremHvordanRettsregelenErAnvendtPaaFaktum,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er mye standardtekst",
                        value = saksdata.kvalitetsvurderingV2.vedtaketIkkeKonkretIndividuellBegrunnelseMyeStandardtekst,
                        type = BOOLEAN
                    )
                )

                add(
                    Field(
                        fieldName = "Språket og formidlingen er ikke tydelig",
                        value = saksdata.kvalitetsvurderingV2.vedtaketSpraakOgFormidlingErIkkeTydelig,
                        type = BOOLEAN
                    )
                )

                //ROL
                add(
                    Field(
                        fieldName = "Bruk av rådgivende lege",
                        value = saksdata.kvalitetsvurderingV2.brukAvRaadgivendeLege,
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege er ikke brukt",
                        value = saksdata.kvalitetsvurderingV2.raadgivendeLegeIkkebrukt,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Saksbehandlers bruk av rådgivende lege er mangelfull",
                        value = saksdata.kvalitetsvurderingV2.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege har uttalt seg om tema utover trygdemedisin",
                        value = saksdata.kvalitetsvurderingV2.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege er brukt, men begrunnelsen fra rådgivende lege er mangelfull eller ikke dokumentert",
                        value = saksdata.kvalitetsvurderingV2.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                        type = BOOLEAN
                    )
                )

                //Annet
                if (includeFritekst) {
                    add(
                        Field(
                            fieldName = "Annet",
                            value = saksdata.kvalitetsvurderingV2.annetFritekst,
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