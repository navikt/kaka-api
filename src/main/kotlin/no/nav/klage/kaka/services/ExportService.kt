package no.nav.klage.kaka.services

import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.services.Field.Type.*
import no.nav.klage.kodeverk.Enhet
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

        val saksdataList = saksdataRepository.findByTilknyttetEnhetInAndAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
            enhetIdList = usersKlageenheter.map { it.id },
            fromDateTime = LocalDate.of(2021, Month.JANUARY, 1).atStartOfDay(),
            toDateTime = LocalDate.of(2022, Month.JANUARY, 1).atStartOfDay(),
        )

//@formatter:off
        val saksdataFields = saksdataList.map { saksdata ->
            val fields = mutableListOf<Field>()
            //Saksdata
            fields += Field(fieldName = "Enhet", value = saksdata.tilknyttetEnhet, type = STRING)
            fields += Field(fieldName = "Sakstype", value = saksdata.sakstype.navn, type = STRING)
            fields += Field(fieldName = "Ytelse", value = saksdata.ytelse!!.navn, type = STRING)
            fields += Field(fieldName = "Mottatt vedtaksinstans", value = saksdata.mottattVedtaksinstans.toString(), type = DATE)
            fields += Field(fieldName = "Mottatt klageinstans", value = saksdata.mottattKlageinstans.toString(), type = DATE)
            fields += Field(fieldName = "Fra vedtaksenhet", value = saksdata.vedtaksinstansEnhet.toEnhetnummer(), type = STRING)
            fields += Field(fieldName = "Utfall/Resultat", value = saksdata.utfall!!.navn, type = STRING)
            //TODO hjemmel toString()
            fields += Field(fieldName = "Hjemmel", value = saksdata.registreringshjemler!!.joinToString(","), type = STRING)

            //Klageforberedelsen
            fields += Field(fieldName = "Klageforberedelsen", value = saksdata.kvalitetsvurdering.klageforberedelsenRadioValg.toString(), type = STRING)
            fields += Field(fieldName = "Sakens dokumenter", value = saksdata.kvalitetsvurdering.sakensDokumenter.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Oversittet klagefrist er ikke kommentert", value = saksdata.kvalitetsvurdering.oversittetKlagefristIkkeKommentert.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Klagerens relevante anførseler er ikke tilstrekkelig kommentert/imøtegått", value = saksdata.kvalitetsvurdering.klagerensRelevanteAnfoerslerIkkeKommentert.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Begrunnelse for hvorfor avslag opprettholdes / klager ikke oppfyller vilkår", value = saksdata.kvalitetsvurdering.begrunnelseForHvorforAvslagOpprettholdes.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Konklusjonen", value = saksdata.kvalitetsvurdering.konklusjonen.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Oversendelsesbrevets innhold er ikke i samsvar med sakens tema", value = saksdata.kvalitetsvurdering.oversendelsesbrevetsInnholdIkkeISamsvarMedTema.toString(), type = BOOLEAN)

            //Utredningen
            fields += Field(fieldName = "Utredningen", value = saksdata.kvalitetsvurdering.utredningenRadioValg.toString(), type = STRING)
            fields += Field(fieldName = "Utredningen av medisinske forhold", value = saksdata.kvalitetsvurdering.utredningenAvMedisinskeForhold.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av medisinske forhold stikkord", value = saksdata.kvalitetsvurdering.utredningenAvMedisinskeForholdText ?: "", type = STRING)
            fields += Field(fieldName = "Utredningen av inntektsforhold", value = saksdata.kvalitetsvurdering.utredningenAvInntektsforhold.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av inntektsforhold stikkord", value = saksdata.kvalitetsvurdering.utredningenAvInntektsforholdText ?: "", type = STRING)
            fields += Field(fieldName = "Utredningen av arbeid", value = saksdata.kvalitetsvurdering.utredningenAvArbeid.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av arbeid stikkord", value = saksdata.kvalitetsvurdering.utredningenAvArbeidText ?: "", type = STRING)
            fields += Field(fieldName = "Arbeidsrettet brukeroppfølging", value = saksdata.kvalitetsvurdering.arbeidsrettetBrukeroppfoelging.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Arbeidsrettet brukeroppfølging stikkord", value = saksdata.kvalitetsvurdering.arbeidsrettetBrukeroppfoelgingText ?: "", type = STRING)
            fields += Field(fieldName = "Utredningen av andre aktuelle forhold i saken", value = saksdata.kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av andre aktuelle forhold i saken stikkord", value = saksdata.kvalitetsvurdering.utredningenAvAndreAktuelleForholdISakenText ?: "", type = STRING)
            fields += Field(fieldName = "Utredningen av EØS / utenlandsproblematikk", value = saksdata.kvalitetsvurdering.utredningenAvEoesProblematikk.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Utredningen av EØS / utenlandsproblematikk stikkord", value = saksdata.kvalitetsvurdering.utredningenAvEoesProblematikkText ?: "", type = STRING)
            fields += Field(fieldName = "Veiledning fra NAV", value = saksdata.kvalitetsvurdering.veiledningFraNav.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Veiledning fra NAV stikkord", value = saksdata.kvalitetsvurdering.veiledningFraNavText ?: "", type = STRING)

            //Vedtaket
            fields += Field(fieldName = "Vedtaket", value = saksdata.kvalitetsvurdering.vedtaketRadioValg.toString(), type = STRING)
            fields += Field(fieldName = "Det er ikke brukt riktig hjemmel(er)", value = saksdata.kvalitetsvurdering.detErIkkeBruktRiktigHjemmel.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet", value = saksdata.kvalitetsvurdering.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Rettsregelen er benyttet eller tolket feil", value = saksdata.kvalitetsvurdering.rettsregelenErBenyttetFeil.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Vurdering av faktum / bevisvurdering er mangelfull", value = saksdata.kvalitetsvurdering.vurderingAvFaktumErMangelfull.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Det er feil i den konkrete rettsanvendelsen", value = saksdata.kvalitetsvurdering.detErFeilIKonkretRettsanvendelse.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Begrunnelsen er ikke konkret og individuell", value = saksdata.kvalitetsvurdering.begrunnelsenErIkkeKonkretOgIndividuell.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Språket/Formidlingen er ikke tydelig", value = saksdata.kvalitetsvurdering.spraaketErIkkeTydelig.toString(), type = BOOLEAN)

            //Annet
            fields += Field(fieldName = "Nye opplysninger mottatt etter oversendelse til klageinstansen", value = saksdata.kvalitetsvurdering.nyeOpplysningerMottatt.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Bruk gjerne vedtaket som eksempel i opplæring", value = saksdata.kvalitetsvurdering.brukIOpplaering.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Bruk gjerne vedtaket som eksempel i opplæring stikkord", value = saksdata.kvalitetsvurdering.brukIOpplaeringText ?: "", type = STRING)

            //ROL
            fields += Field(fieldName = "Bruk av rådgivende lege", value = saksdata.kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg.toString(), type = STRING)
            fields += Field(fieldName = "Rådgivende lege er ikke brukt", value = saksdata.kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg.toString(), type = STRING)
            fields += Field(fieldName = "Rådgivende lege er brukt, men saksbehandler har stilt feil spørsmål og får derfor feil svar", value = saksdata.kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Rådgivende lege har uttalt seg om tema utover trygdemedisin", value = saksdata.kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg.toString(), type = BOOLEAN)
            fields += Field(fieldName = "Rådgivende lege er brukt, men dokumentasjonen er mangelfull / ikke skriftliggjort", value = saksdata.kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg.toString(), type = BOOLEAN)

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
        var rowCounter = 1

        val style = workbook.createCellStyle()
        style.wrapText = true
        saksdataFields.forEach { d ->
            val row = sheet.createRow(rowCounter++)

            var columnCounter = 0

            d.forEach { column ->
                val cell = row.createCell(columnCounter++)
                cell.setCellValue(column.value)
                cell.setCellType(column.type.toCellType())
                cell.cellStyle = style
            }
        }

        val baos = ByteArrayOutputStream()
        workbook.write(baos)
        return baos.toByteArray()
    }
}

private fun Field.Type.toCellType(): CellType = when (this) {
    BOOLEAN -> CellType.BOOLEAN
    //TODO date/calendar type
    DATE -> CellType.STRING
    else -> CellType.STRING
}

private fun String?.toEnhetnummer(): String {
    return Enhet.values().find { it.id == this }!!.navn
}


data class Field(val fieldName: String, val value: String, val type: Type) {
    enum class Type {
        STRING, NUMBER, BOOLEAN, DATE
    }
}
