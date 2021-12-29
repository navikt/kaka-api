package no.nav.klage.kaka.services

import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.services.Field.Type.*
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.Month


@Service
class ExportService(private val saksdataRepository: SaksdataRepository) {

    fun getAsExcel(usersKlageenheter: List<Enhet>): ByteArray {
        val year = 2021

        val saksdataList =
            saksdataRepository.findByTilknyttetEnhetInAndAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                enhetIdList = usersKlageenheter.map { it.id },
                fromDateTime = LocalDate.of(2021, Month.JANUARY, 1).atStartOfDay(),
                toDateTime = LocalDate.of(2022, Month.JANUARY, 1).atStartOfDay(),
            )

//@formatter:off
        val saksdataFields = saksdataList.map { saksdata ->
            val fields = mutableListOf<Field>()
            //Saksdata
            fields += Field(fieldName = "Enhet", value = saksdata.tilknyttetEnhet.toEnhetnummer(), type = STRING)
            fields += Field(fieldName = "Sakstype", value = saksdata.sakstype.navn, type = STRING)
            fields += Field(fieldName = "Ytelse", value = saksdata.ytelse!!.navn, type = STRING)
            fields += Field(fieldName = "Mottatt vedtaksinstans", value = saksdata.mottattVedtaksinstans, type = DATE)
            fields += Field(fieldName = "Mottatt klageinstans", value = saksdata.mottattKlageinstans, type = DATE)
            fields += Field(fieldName = "Fra vedtaksenhet", value = saksdata.vedtaksinstansEnhet.toEnhetnummer(), type = STRING)
            fields += Field(fieldName = "Utfall/Resultat", value = saksdata.utfall!!.navn, type = STRING)
            fields += Field(fieldName = "Hjemmel", value = saksdata.registreringshjemler.toHjemlerString(), type = STRING)

            //Klageforberedelsen
            fields += Field(fieldName = "Klageforberedelsen", value = saksdata.kvalitetsvurdering.klageforberedelsenRadioValg, type = STRING)
            fields += Field(fieldName = "Sakens dokumenter", value = saksdata.kvalitetsvurdering.sakensDokumenter, type = BOOLEAN)
            fields += Field(fieldName = "Oversittet klagefrist er ikke kommentert", value = saksdata.kvalitetsvurdering.oversittetKlagefristIkkeKommentert, type = BOOLEAN)
            fields += Field(fieldName = "Klagerens relevante anførseler er ikke tilstrekkelig kommentert/imøtegått", value = saksdata.kvalitetsvurdering.klagerensRelevanteAnfoerslerIkkeKommentert, type = BOOLEAN)
            fields += Field(fieldName = "Begrunnelse for hvorfor avslag opprettholdes / klager ikke oppfyller vilkår", value = saksdata.kvalitetsvurdering.begrunnelseForHvorforAvslagOpprettholdes, type = BOOLEAN)
            fields += Field(fieldName = "Konklusjonen", value = saksdata.kvalitetsvurdering.konklusjonen, type = BOOLEAN)
            fields += Field(fieldName = "Oversendelsesbrevets innhold er ikke i samsvar med sakens tema", value = saksdata.kvalitetsvurdering.oversendelsesbrevetsInnholdIkkeISamsvarMedTema, type = BOOLEAN)

            //Utredningen
            fields += Field(fieldName = "Utredningen", value = saksdata.kvalitetsvurdering.utredningenRadioValg, type = STRING)
            fields += Field(fieldName = "Utredningen av medisinske forhold", value = saksdata.kvalitetsvurdering.utredningenAvMedisinskeForhold, type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av medisinske forhold stikkord", value = saksdata.kvalitetsvurdering.utredningenAvMedisinskeForholdText, type = STRING)
            fields += Field(fieldName = "Utredningen av inntektsforhold", value = saksdata.kvalitetsvurdering.utredningenAvInntektsforhold, type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av inntektsforhold stikkord", value = saksdata.kvalitetsvurdering.utredningenAvInntektsforholdText, type = STRING)
            fields += Field(fieldName = "Utredningen av arbeid", value = saksdata.kvalitetsvurdering.utredningenAvArbeid, type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av arbeid stikkord", value = saksdata.kvalitetsvurdering.utredningenAvArbeidText, type = STRING)
            fields += Field(fieldName = "Arbeidsrettet brukeroppfølging", value = saksdata.kvalitetsvurdering.arbeidsrettetBrukeroppfoelging, type = BOOLEAN)
            fields += Field(fieldName = "Arbeidsrettet brukeroppfølging stikkord", value = saksdata.kvalitetsvurdering.arbeidsrettetBrukeroppfoelgingText, type = STRING)
            fields += Field(fieldName = "Utredningen av andre aktuelle forhold i saken", value = saksdata.kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken, type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av andre aktuelle forhold i saken stikkord", value = saksdata.kvalitetsvurdering.utredningenAvAndreAktuelleForholdISakenText, type = STRING)
            fields += Field(fieldName = "Utredningen av EØS / utenlandsproblematikk", value = saksdata.kvalitetsvurdering.utredningenAvEoesProblematikk, type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av EØS / utenlandsproblematikk stikkord", value = saksdata.kvalitetsvurdering.utredningenAvEoesProblematikkText, type = STRING)
            fields += Field(fieldName = "Veiledning fra NAV", value = saksdata.kvalitetsvurdering.veiledningFraNav, type = BOOLEAN)
            fields += Field(fieldName = "Veiledning fra NAV stikkord", value = saksdata.kvalitetsvurdering.veiledningFraNavText, type = STRING)

            //Vedtaket
            fields += Field(fieldName = "Vedtaket", value = saksdata.kvalitetsvurdering.vedtaketRadioValg, type = STRING)
            fields += Field(fieldName = "Det er ikke brukt riktig hjemmel(er)", value = saksdata.kvalitetsvurdering.detErIkkeBruktRiktigHjemmel, type = BOOLEAN)
            fields += Field(fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet", value = saksdata.kvalitetsvurdering.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet, type = BOOLEAN)
            fields += Field(fieldName = "Rettsregelen er benyttet eller tolket feil", value = saksdata.kvalitetsvurdering.rettsregelenErBenyttetFeil, type = BOOLEAN)
            fields += Field(fieldName = "Vurdering av faktum / bevisvurdering er mangelfull", value = saksdata.kvalitetsvurdering.vurderingAvFaktumErMangelfull, type = BOOLEAN)
            fields += Field(fieldName = "Det er feil i den konkrete rettsanvendelsen", value = saksdata.kvalitetsvurdering.detErFeilIKonkretRettsanvendelse, type = BOOLEAN)
            fields += Field(fieldName = "Begrunnelsen er ikke konkret og individuell", value = saksdata.kvalitetsvurdering.begrunnelsenErIkkeKonkretOgIndividuell, type = BOOLEAN)
            fields += Field(fieldName = "Språket/Formidlingen er ikke tydelig", value = saksdata.kvalitetsvurdering.spraaketErIkkeTydelig, type = BOOLEAN)

            //Annet
            fields += Field(fieldName = "Nye opplysninger mottatt etter oversendelse til klageinstansen", value = saksdata.kvalitetsvurdering.nyeOpplysningerMottatt, type = BOOLEAN)
            fields += Field(fieldName = "Bruk gjerne vedtaket som eksempel i opplæring", value = saksdata.kvalitetsvurdering.brukIOpplaering, type = BOOLEAN)
            fields += Field(fieldName = "Bruk gjerne vedtaket som eksempel i opplæring stikkord", value = saksdata.kvalitetsvurdering.brukIOpplaeringText, type = STRING)

            //ROL
            fields += Field(fieldName = "Bruk av rådgivende lege", value = saksdata.kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg, type = STRING)
            fields += Field(fieldName = "Rådgivende lege er ikke brukt", value = saksdata.kvalitetsvurdering.raadgivendeLegeErIkkeBrukt, type = BOOLEAN)
            fields += Field(fieldName = "Rådgivende lege er brukt, men saksbehandler har stilt feil spørsmål og får derfor feil svar", value = saksdata.kvalitetsvurdering.raadgivendeLegeErBruktFeilSpoersmaal, type = BOOLEAN)
            fields += Field(fieldName = "Rådgivende lege har uttalt seg om tema utover trygdemedisin", value = saksdata.kvalitetsvurdering.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin, type = BOOLEAN)
            fields += Field(fieldName = "Rådgivende lege er brukt, men dokumentasjonen er mangelfull / ikke skriftliggjort", value = saksdata.kvalitetsvurdering.raadgivendeLegeErBruktMangelfullDokumentasjon, type = BOOLEAN)

            fields
        }
//@formatter:on

        val workbook: Workbook = XSSFWorkbook()

        val sheet: Sheet = workbook.createSheet("Statistikk år $year")
        repeat(45) {
            sheet.setColumnWidth(it, 6000)
        }

        val header: Row = sheet.createRow(0)

        val headerStyle: CellStyle = workbook.createCellStyle()
//        headerStyle.fillForegroundColor = IndexedColors.LIGHT_BLUE.getIndex()
//        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)

        val font: XSSFFont = (workbook as XSSFWorkbook).createFont()
        font.fontName = "Arial"
//        font.fontHeightInPoints = 16.toShort()
        font.bold = true
        headerStyle.setFont(font)

        var headerCounter = 0

        saksdataFields.first().forEach { headerColumns ->
            val headerCell = header.createCell(headerCounter++)
            headerCell.setCellValue(headerColumns.fieldName)
            headerCell.cellStyle = headerStyle
        }

        //Cells
        val createHelper: CreationHelper = workbook.creationHelper
        var rowCounter = 1

        saksdataFields.forEach { saksdataRow ->
            val row = sheet.createRow(rowCounter++)

            var columnCounter = 0

            saksdataRow.forEach { column ->
                val style = workbook.createCellStyle()
                val cell = row.createCell(columnCounter++)

                when (column.type) {
                    DATE -> {
                        if (column.value != null) {
                            cell.setCellValue((column.value as LocalDate))
                        }
                        style.dataFormat = createHelper.createDataFormat().getFormat("yyyy-mm-dd")
                    }
                    BOOLEAN -> {
                        cell.setCellValue(column.value as Boolean)
                    }
                    else -> {
                        style.wrapText = true
                        cell.setCellValue(column.value?.toString() ?: "")
                    }
                }

                cell.cellStyle = style
            }
        }

        val baos = ByteArrayOutputStream()
        workbook.write(baos)
        return baos.toByteArray()
    }
}

private fun Set<Registreringshjemmel>?.toHjemlerString() =
    this?.joinToString { "${it.lovKilde.beskrivelse} - ${it.spesifikasjon}" } ?: ""

private fun String?.toEnhetnummer(): String {
    return Enhet.values().find { it.id == this }!!.navn
}

data class Field(val fieldName: String, val value: Any?, val type: Type) {
    enum class Type {
        STRING, NUMBER, BOOLEAN, DATE
    }
}