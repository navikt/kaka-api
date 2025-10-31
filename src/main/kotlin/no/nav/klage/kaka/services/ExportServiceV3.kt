package no.nav.klage.kaka.services

import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingV3
import no.nav.klage.kaka.api.view.AnonymizedFinishedVurderingWithoutEnheterV3
import no.nav.klage.kaka.api.view.Date
import no.nav.klage.kaka.api.view.ExcelQueryParams
import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.domain.Vedtaksinstansgruppe
import no.nav.klage.kaka.domain.kvalitetsvurdering.v3.KvalitetsvurderingV3
import no.nav.klage.kaka.domain.vedtaksinstansgruppeMap
import no.nav.klage.kaka.exceptions.MissingTilgangException
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.repositories.SaksdataRepositoryCustomImpl
import no.nav.klage.kaka.services.ExportServiceV3.Field.Type.*
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
class ExportServiceV3(
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
        val resultList = saksdataRepository.findByQueryParamsV3(
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
    fun getFinishedForLederAsRawData(
        enhet: Enhet,
        fromMonth: YearMonth,
        toMonth: YearMonth,
        saksbehandlerIdentList: List<String>?
    ): AnonymizedManagerResponseV3 {
        validateNotCurrentMonth(toMonth)

        val fromDateTime = fromMonth.atDay(1).atStartOfDay()
        val toDateTime = toMonth.atEndOfMonth().atTime(LocalTime.MAX)

        val resultList = saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV3(
            fromDateTime = fromDateTime,
            toDateTime = toDateTime,
        )

        return if (!saksbehandlerIdentList.isNullOrEmpty()) {
            val (mine, rest) = resultList.filter { it.saksdata.utfoerendeSaksbehandler !in saksbehandlerIdentList }
                .partition { it.saksdata.tilknyttetEnhet == enhet.navn }

            val saksbehandlerMap = saksbehandlerIdentList.associateWith { _ ->
                emptyList<AnonymizedFinishedVurderingV3>()
            }.toMutableMap()

            //Replace those who have data
            resultList.groupBy { it.saksdata.utfoerendeSaksbehandler }.forEach {
                if (saksbehandlerMap.containsKey(it.key)) {
                    saksbehandlerMap[it.key] = privateGetFinishedAsRawData(resultList = it.value.toSet())
                }
            }

            AnonymizedManagerResponseV3(
                saksbehandlere = saksbehandlerMap,
                mine = privateGetFinishedAsRawData(resultList = mine.toSet()),
                rest = privateGetFinishedAsRawData(resultList = rest.toSet()),
            )
        } else {
            val (mine, rest) = resultList.partition { it.saksdata.tilknyttetEnhet == enhet.navn }
            AnonymizedManagerResponseV3(
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
    fun getFinishedAsRawDataByDates(fromDate: LocalDate, toDate: LocalDate): List<AnonymizedFinishedVurderingV3> {
        val resultList =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV3(
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
    ): List<AnonymizedFinishedVurderingWithoutEnheterV3> {
        val resultList =
            saksdataRepository.findByAvsluttetAvSaksbehandlerBetweenV3(
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
    ): AnonymizedVedtaksinstanslederResponseV3 {
        val resultList =
            saksdataRepository.findForVedtaksinstanslederV3(
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX),
                mangelfullt = mangelfullt,
            )

        val (mine, rest) = resultList.partition { it.saksdata.vedtaksinstansEnhet == vedtaksinstansEnhet.navn }

        return AnonymizedVedtaksinstanslederResponseV3(
            mine = privateGetFinishedAsRawDataWithoutEnheterWithVersion3(resultList = mine.toSet()),
            rest = privateGetFinishedAsRawDataWithoutEnheterWithVersion3(resultList = rest.toSet()),
        )
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr or navIdent)) based on given dates and klageenhet,
     * partitioned by saksbehandler (mine vs rest)
     */
    fun getFinishedAsRawDataByDatesAndKlageenhetPartitionedBySaksbehandler(
        fromDate: LocalDate,
        toDate: LocalDate,
        saksbehandler: String,
        enhet: Enhet,
    ): AnonymizedMineRestResponseV3 {
        val resultList =
            saksdataRepository.findByTilknyttetEnhetAndAvsluttetAvSaksbehandlerBetweenOrderByCreatedV3(
                fromDateTime = fromDate.atStartOfDay(),
                toDateTime = toDate.atTime(LocalTime.MAX),
                enhet = enhet.navn,
            )

        val (mine, rest) = resultList.partition { it.saksdata.utfoerendeSaksbehandler == saksbehandler }

        return AnonymizedMineRestResponseV3(
            mine = privateGetFinishedAsRawData(resultList = mine.toSet()),
            rest = privateGetFinishedAsRawData(resultList = rest.toSet()),
        )
    }

    private fun privateGetFinishedAsRawData(
        resultList: Set<SaksdataRepositoryCustomImpl.QueryResultV3>,
    ): List<AnonymizedFinishedVurderingV3> {

        return resultList.map { result ->
            val (saksdata, kvalitetsvurderingV3) = result

            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            AnonymizedFinishedVurderingV3(
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

                // Særregelverket
                saerregelverkAutomatiskVedtak = kvalitetsvurderingV3.saerregelverkAutomatiskVedtak,
                saerregelverk = kvalitetsvurderingV3.saerregelverk?.name,
                saerregelverkLovenErTolketEllerAnvendtFeil = kvalitetsvurderingV3.saerregelverkLovenErTolketEllerAnvendtFeil,
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning,
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList?.map { it.id },
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn,
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList?.map { it.id },
                saerregelverkDetErLagtTilGrunnFeilFaktum = kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktum,
                saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList = kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList?.map { it.id },

                // Saksbehandlingsregler
                saksbehandlingsregler = kvalitetsvurderingV3.saksbehandlingsregler?.name,
                saksbehandlingsreglerBruddPaaVeiledningsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaVeiledningsplikten,
                saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser = kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser,
                saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning = kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning,
                saksbehandlingsreglerBruddPaaUtredningsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaUtredningsplikten,
                saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok,
                saksbehandlingsreglerBruddPaaForeleggelsesplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaForeleggelsesplikten,
                saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten = kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten,
                saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten = kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten,
                saksbehandlingsreglerBruddPaaBegrunnelsesplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaBegrunnelsesplikten,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList?.map { it.id },
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList?.map { it.id },
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList?.map { it.id },
                saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse,
                saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert,
                saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold,
                saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser,
                saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke,
                saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert = kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert,
                saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform = kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform,
                saksbehandlingsreglerBruddPaaJournalfoeringsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaJournalfoeringsplikten,
                saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert = kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert,
                saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet = kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet,
                saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak,
                saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok,
                saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok,

                // Trygdemedisin
                brukAvRaadgivendeLege = kvalitetsvurderingV3.brukAvRaadgivendeLege?.name,
                raadgivendeLegeIkkebrukt = kvalitetsvurderingV3.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = kvalitetsvurderingV3.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = kvalitetsvurderingV3.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = kvalitetsvurderingV3.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,

                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata, kvalitetsvurderingV3),
                modifiedDate = getModifiedDate(saksdata, kvalitetsvurderingV3),
            )
        }
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr, navIdent or enheter)) based on given dates.
     */
    private fun privateGetFinishedAsRawDataWithoutEnheter(
        resultList: Set<SaksdataRepositoryCustomImpl.QueryResultV3>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV3> {

        return resultList.map { result ->
            val (saksdata, kvalitetsvurderingV3) = result

            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay
            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            AnonymizedFinishedVurderingWithoutEnheterV3(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                hjemmelIdList = saksdata.registreringshjemler!!.map { it.id },
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandlerDate,
                ytelseId = saksdata.ytelse!!.id,
                utfallId = saksdata.utfall!!.id,
                sakstypeId = saksdata.sakstype.id,
                mottattVedtaksinstans = saksdata.mottattVedtaksinstans?.toDate(),
                mottattKlageinstans = mottattKlageinstansDate,
                tilbakekreving = saksdata.tilbakekreving,

                // Særregelverket
                saerregelverkAutomatiskVedtak = kvalitetsvurderingV3.saerregelverkAutomatiskVedtak,
                saerregelverk = kvalitetsvurderingV3.saerregelverk?.name,
                saerregelverkLovenErTolketEllerAnvendtFeil = kvalitetsvurderingV3.saerregelverkLovenErTolketEllerAnvendtFeil,
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning,
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList?.map { it.id },
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn,
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList?.map { it.id },
                saerregelverkDetErLagtTilGrunnFeilFaktum = kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktum,
                saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList = kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList?.map { it.id },

                // Saksbehandlingsregler
                saksbehandlingsregler = kvalitetsvurderingV3.saksbehandlingsregler?.name,
                saksbehandlingsreglerBruddPaaVeiledningsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaVeiledningsplikten,
                saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser = kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser,
                saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning = kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning,
                saksbehandlingsreglerBruddPaaUtredningsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaUtredningsplikten,
                saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok,
                saksbehandlingsreglerBruddPaaForeleggelsesplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaForeleggelsesplikten,
                saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten = kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten,
                saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten = kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten,
                saksbehandlingsreglerBruddPaaBegrunnelsesplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaBegrunnelsesplikten,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList?.map { it.id },
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList?.map { it.id },
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList?.map { it.id },
                saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse,
                saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert,
                saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold,
                saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser,
                saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke,
                saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert = kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert,
                saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform = kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform,
                saksbehandlingsreglerBruddPaaJournalfoeringsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaJournalfoeringsplikten,
                saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert = kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert,
                saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet = kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet,
                saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak,
                saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok,
                saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok,

                // Trygdemedisin
                brukAvRaadgivendeLege = kvalitetsvurderingV3.brukAvRaadgivendeLege?.name,
                raadgivendeLegeIkkebrukt = kvalitetsvurderingV3.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = kvalitetsvurderingV3.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = kvalitetsvurderingV3.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = kvalitetsvurderingV3.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,

                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata, kvalitetsvurderingV3),
                modifiedDate = getModifiedDate(saksdata, kvalitetsvurderingV3),
            )
        }
    }

    /**
     * Return all 'finished' saksdata (anonymized (no fnr, navIdent or enheter)) based on given dates.
     * This version validates that the kvalitetsvurdering is version 3.
     */
    private fun privateGetFinishedAsRawDataWithoutEnheterWithVersion3(
        resultList: Set<SaksdataRepositoryCustomImpl.QueryResultV3>,
    ): List<AnonymizedFinishedVurderingWithoutEnheterV3> {

        return resultList.map { result ->
            val (saksdata, kvalitetsvurderingV3) = result

            if (saksdata.kvalitetsvurderingReference.version != 3) {
                error("This query only works for version 3 of kvalitetsvurderinger")
            }

            val mottattKlageinstansDate = saksdata.mottattKlageinstans!!.toDate()
            val avsluttetAvSaksbehandlerDate = saksdata.avsluttetAvSaksbehandler!!.toDate()

            val mottattForrigeInstans = getMottattForrigeInstans(saksdata)

            val kaBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattKlageinstansDate.epochDay
            val totalBehandlingstidDays = avsluttetAvSaksbehandlerDate.epochDay - mottattForrigeInstans.epochDay

            val vedtaksinstansBehandlingstidDays = getVedtaksinstansBehandlingstidDays(saksdata)

            AnonymizedFinishedVurderingWithoutEnheterV3(
                id = UUID.nameUUIDFromBytes(saksdata.id.toString().toByteArray()),
                hjemmelIdList = saksdata.registreringshjemler!!.map { it.id },
                avsluttetAvSaksbehandler = avsluttetAvSaksbehandlerDate,
                ytelseId = saksdata.ytelse!!.id,
                utfallId = saksdata.utfall!!.id,
                sakstypeId = saksdata.sakstype.id,
                mottattVedtaksinstans = saksdata.mottattVedtaksinstans?.toDate(),
                mottattKlageinstans = mottattKlageinstansDate,
                tilbakekreving = saksdata.tilbakekreving,

                // Særregelverket
                saerregelverkAutomatiskVedtak = kvalitetsvurderingV3.saerregelverkAutomatiskVedtak,
                saerregelverk = kvalitetsvurderingV3.saerregelverk?.name,
                saerregelverkLovenErTolketEllerAnvendtFeil = kvalitetsvurderingV3.saerregelverkLovenErTolketEllerAnvendtFeil,
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning,
                saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList?.map { it.id },
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn,
                saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList?.map { it.id },
                saerregelverkDetErLagtTilGrunnFeilFaktum = kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktum,
                saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList = kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList?.map { it.id },

                // Saksbehandlingsregler
                saksbehandlingsregler = kvalitetsvurderingV3.saksbehandlingsregler?.name,
                saksbehandlingsreglerBruddPaaVeiledningsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaVeiledningsplikten,
                saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser = kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser,
                saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning = kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning,
                saksbehandlingsreglerBruddPaaUtredningsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaUtredningsplikten,
                saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok,
                saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok,
                saksbehandlingsreglerBruddPaaForeleggelsesplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaForeleggelsesplikten,
                saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten = kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten,
                saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten = kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten,
                saksbehandlingsreglerBruddPaaBegrunnelsesplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaBegrunnelsesplikten,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList?.map { it.id },
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList?.map { it.id },
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn,
                saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList?.map { it.id },
                saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse,
                saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert,
                saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold,
                saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser,
                saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke,
                saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert = kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert,
                saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform = kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform,
                saksbehandlingsreglerBruddPaaJournalfoeringsplikten = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaJournalfoeringsplikten,
                saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert = kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert,
                saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet = kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet,
                saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak,
                saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok,
                saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok,

                // Trygdemedisin
                brukAvRaadgivendeLege = kvalitetsvurderingV3.brukAvRaadgivendeLege?.name,
                raadgivendeLegeIkkebrukt = kvalitetsvurderingV3.raadgivendeLegeIkkebrukt,
                raadgivendeLegeMangelfullBrukAvRaadgivendeLege = kvalitetsvurderingV3.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin = kvalitetsvurderingV3.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert = kvalitetsvurderingV3.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,

                kaBehandlingstidDays = kaBehandlingstidDays,
                vedtaksinstansBehandlingstidDays = vedtaksinstansBehandlingstidDays,
                totalBehandlingstidDays = totalBehandlingstidDays,
                createdDate = getCreatedDate(saksdata, kvalitetsvurderingV3),
                modifiedDate = getModifiedDate(saksdata, kvalitetsvurderingV3),
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

    private fun getVedtaksinstansBehandlingstidDays(saksdata: Saksdata): Int {
        return if (saksdata.sakstype == Type.KLAGE) {
            saksdata.mottattKlageinstans!!.toDate().epochDay - saksdata.mottattVedtaksinstans!!.toEpochDay().toInt()
        } else {
            //FE wants 0 for anker as of now.
            0
        }
    }

    private fun getMottattForrigeInstans(saksdata: Saksdata): Date {
        val mottattForrigeInstans = if (saksdata.sakstype in listOf(
                Type.ANKE,
                Type.BEHANDLING_ETTER_TRYGDERETTEN_OPPHEVET,
                Type.OMGJOERINGSKRAV,
                Type.BEGJAERING_OM_GJENOPPTAK
            )
        ) {
            saksdata.mottattKlageinstans!!.toDate()
        } else {
            saksdata.mottattVedtaksinstans!!.toDate()
        }
        return mottattForrigeInstans
    }

    private fun getCreatedDate(
        saksdata: Saksdata,
        kvalitetsvurderingV3: KvalitetsvurderingV3? = null
    ): Date {
        if (kvalitetsvurderingV3 == null) {
            return saksdata.created.toDate()
        }

        return if (saksdata.created.isBefore(kvalitetsvurderingV3.created)) {
            saksdata.created.toDate()
        } else {
            kvalitetsvurderingV3.created.toDate()
        }
    }

    private fun getModifiedDate(
        saksdata: Saksdata,
        kvalitetsvurderingV3: KvalitetsvurderingV3? = null
    ): Date {
        if (kvalitetsvurderingV3 == null) {
            return saksdata.created.toDate()
        }

        return if (saksdata.modified.isAfter(kvalitetsvurderingV3.modified)) {
            saksdata.modified.toDate()
        } else {
            kvalitetsvurderingV3.modified.toDate()
        }
    }

    private fun mapToFields(
        saksdataList: Set<SaksdataRepositoryCustomImpl.QueryResultV3>,
        includeFritekst: Boolean
    ): List<List<Field>> {
        //@formatter:off
        return saksdataList.map { result ->
            val (saksdata, kvalitetsvurderingV3) = result
            if (saksdata.kvalitetsvurderingReference.version != 3) {
                error("This query only works for version 3 of kvalitetsvurderinger")
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

                //Særregelverket - Automatisk vedtak
                add(
                    Field(
                        fieldName = "Avhuking for automatiske vedtak",
                        value = kvalitetsvurderingV3.saerregelverkAutomatiskVedtak,
                        type = BOOLEAN
                    )
                )

                //Særregelverket
                add(
                    Field(
                        fieldName = "Særregelverket",
                        value = kvalitetsvurderingV3.saerregelverk,
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Loven er tolket eller anvendt feil",
                        value = kvalitetsvurderingV3.saerregelverkLovenErTolketEllerAnvendtFeil,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Vedtaket bygger på feil hjemmel eller lovtolkning",
                        value = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkning,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Vedtaket bygger på feil hjemmel eller lovtolkning - hjemler",
                        value = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilHjemmelEllerLovtolkningHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Vedtaket bygger på feil konkret rettsanvendelse eller skjønn",
                        value = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoenn,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Vedtaket bygger på feil konkret rettsanvendelse eller skjønn - hjemler",
                        value = kvalitetsvurderingV3.saerregelverkVedtaketByggerPaaFeilKonkretRettsanvendelseEllerSkjoennHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Det er lagt til grunn feil faktum",
                        value = kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktum,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er lagt til grunn feil faktum - hjemler",
                        value = kvalitetsvurderingV3.saerregelverkDetErLagtTilGrunnFeilFaktumHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )

                //Saksbehandlingsregler
                add(
                    Field(
                        fieldName = "Saksbehandlingsregler",
                        value = kvalitetsvurderingV3.saksbehandlingsregler,
                        type = STRING
                    )
                )

                // Brudd på veiledningsplikten
                add(
                    Field(
                        fieldName = "Brudd på veiledningsplikten",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaVeiledningsplikten,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Parten har ikke fått svar på henvendelser",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenPartenHarIkkeFaattSvarPaaHenvendelser,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "NAV har ikke gitt god nok veiledning",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerVeiledningspliktenNavHarIkkeGittGodNokVeiledning,
                        type = BOOLEAN
                    )
                )

                // Brudd på utredningsplikten
                add(
                    Field(
                        fieldName = "Brudd på utredningsplikten",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaUtredningsplikten,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av medisinske forhold har ikke vært god nok",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvMedisinskeForholdHarIkkeVaertGodNok,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av inntekts-/arbeidsforhold har ikke vært god nok",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvInntektsArbeidsforholdHarIkkeVaertGodNok,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av EØS-/utenlandsforhold har ikke vært god nok",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvEoesUtenlandsforholdHarIkkeVaertGodNok,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av sivilstands-/boforhold har ikke vært god nok",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSivilstandsBoforholdHarIkkeVaertGodNok,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av samværsforhold har ikke vært god nok",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvSamvaersforholdHarIkkeVaertGodNok,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Utredningen av andre forhold i saken har ikke vært god nok",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerUtredningspliktenUtredningenAvAndreForholdISakenHarIkkeVaertGodNok,
                        type = BOOLEAN
                    )
                )

                // Brudd på foreleggelsesplikten
                add(
                    Field(
                        fieldName = "Brudd på foreleggelsesplikten",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaForeleggelsesplikten,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Uttalelse fra rådgivende lege har ikke vært forelagt parten",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenUttalelseFraRaadgivendeLegeHarIkkeVaertForelagtParten,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Andre opplysninger i saken har ikke vært forelagt parten",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerForeleggelsespliktenAndreOpplysningerISakenHarIkkeVaertForelagtParten,
                        type = BOOLEAN
                    )
                )

                // Brudd på begrunnelsesplikten
                add(
                    Field(
                        fieldName = "Brudd på begrunnelsesplikten",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaBegrunnelsesplikten,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Begrunnelsen viser ikke til regelverket",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverket,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Begrunnelsen viser ikke til regelverket - hjemler",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenViserIkkeTilRegelverketHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Begrunnelsen nevner ikke faktum",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktum,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Begrunnelsen nevner ikke faktum - hjemler",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeFaktumHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Begrunnelsen nevner ikke avgjørende hensyn",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensyn,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Begrunnelsen nevner ikke avgjørende hensyn - hjemler",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBegrunnelsespliktenBegrunnelsenNevnerIkkeAvgjoerendeHensynHjemlerList.toHjemlerString(),
                        type = STRING
                    )
                )

                // Brudd på reglene om klage og klageforberedelse
                add(
                    Field(
                        fieldName = "Brudd på reglene om klage og klageforberedelse",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmKlageOgKlageforberedelse,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Klagefristen eller oppreisning er ikke vurdert eller feil vurdert",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageKlagefristenEllerOppreisningErIkkeVurdertEllerFeilVurdert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er ikke sørget for retting av feil i klagens form eller innhold",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageDetErIkkeSoergetForRettingAvFeilIKlagensFormEllerInnhold,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Under klageforberedelsen er det ikke utredet eller gjort undersøkelser",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlageUnderKlageforberedelsenErDetIkkeUtredetEllerGjortUndersoekelser,
                        type = BOOLEAN
                    )
                )

                // Brudd på reglene om omgjøring
                add(
                    Field(
                        fieldName = "Brudd på reglene om omgjøring utenfor klage og anke",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaRegleneOmOmgjoeringUtenforKlageOgAnke,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Ugyldighet og omgjøring er ikke vurdert eller feil vurdert",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringUgyldighetOgOmgjoeringErIkkeVurdertEllerFeilVurdert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Det er fattet vedtak til tross for at beslutning var riktig avgjørelsesform",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerOmgjoeringDetErFattetVedtakTilTrossForAtBeslutningVarRiktigAvgjoerelsesform,
                        type = BOOLEAN
                    )
                )

                // Brudd på journalføringsplikten
                add(
                    Field(
                        fieldName = "Brudd på journalføringsplikten",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaJournalfoeringsplikten,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Relevante opplysninger er ikke journalført",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerErIkkeJournalfoert,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Relevante opplysninger har ikke god nok tittel eller dokumentkvalitet",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerJournalfoeringspliktenRelevanteOpplysningerHarIkkeGodNokTittelEllerDokumentkvalitet,
                        type = BOOLEAN
                    )
                )

                // Brudd på klart språk
                add(
                    Field(
                        fieldName = "Brudd på plikten til å kommunisere på et klart språk",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaPliktTilAaKommuniserePaaEtKlartSpraak,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Språket i vedtaket er ikke klart nok",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIVedtaketErIkkeKlartNok,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Språket i oversendelsesbrevet er ikke klart nok",
                        value = kvalitetsvurderingV3.saksbehandlingsreglerBruddPaaKlartSprakSpraketIOversendelsesbrevetsErIkkeKlartNok,
                        type = BOOLEAN
                    )
                )

                //Bruk av rådgivende lege (Trygdemedisin)
                add(
                    Field(
                        fieldName = "Bruk av rådgivende lege",
                        value = kvalitetsvurderingV3.brukAvRaadgivendeLege,
                        type = STRING
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege er ikke brukt",
                        value = kvalitetsvurderingV3.raadgivendeLegeIkkebrukt,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Saksbehandlers bruk av rådgivende lege er mangelfull",
                        value = kvalitetsvurderingV3.raadgivendeLegeMangelfullBrukAvRaadgivendeLege,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege har uttalt seg om tema utover trygdemedisin",
                        value = kvalitetsvurderingV3.raadgivendeLegeUttaltSegOmTemaUtoverTrygdemedisin,
                        type = BOOLEAN
                    )
                )
                add(
                    Field(
                        fieldName = "Rådgivende lege er brukt, men begrunnelsen fra rådgivende lege er mangelfull eller ikke dokumentert",
                        value = kvalitetsvurderingV3.raadgivendeLegeBegrunnelseMangelfullEllerIkkeDokumentert,
                        type = BOOLEAN
                    )
                )

                //Annet
                if (includeFritekst) {
                    add(
                        Field(
                            fieldName = "Annet",
                            value = kvalitetsvurderingV3.annetFritekst,
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

data class AnonymizedManagerResponseV3(
    val saksbehandlere: Map<String, List<AnonymizedFinishedVurderingV3>>,
    val mine: List<AnonymizedFinishedVurderingV3>,
    val rest: List<AnonymizedFinishedVurderingV3>,
)

data class AnonymizedMineRestResponseV3(
    val mine: List<AnonymizedFinishedVurderingV3>,
    val rest: List<AnonymizedFinishedVurderingV3>,
)

data class AnonymizedVedtaksinstanslederResponseV3(
    val mine: List<AnonymizedFinishedVurderingWithoutEnheterV3>,
    val rest: List<AnonymizedFinishedVurderingWithoutEnheterV3>,
)