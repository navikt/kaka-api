package no.nav.klage.kaka.services

import no.nav.klage.kaka.domain.Saksdata
import no.nav.klage.kaka.repositories.SaksdataRepository
import no.nav.klage.kaka.services.ExportService.Field.Type.*
import no.nav.klage.kodeverk.Enhet
import no.nav.klage.kodeverk.hjemmel.Registreringshjemmel
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.Year


@Service
class ExportService(private val saksdataRepository: SaksdataRepository) {

    /**
     * Returns excel-report based on who user is
     */
    fun getAsExcel(usersKlageenheter: List<Enhet>, year: Year = Year.now(), roles: List<String>): ByteArray {
        var saksdataList = emptyList<Saksdata>()

        if ("ROLE_KLAGE_LEDER" in roles) {
            saksdataList =
                saksdataRepository.findByTilknyttetEnhetInAndAndAvsluttetAvSaksbehandlerBetweenOrderByCreated(
                    enhetIdList = usersKlageenheter.map { it.id },
                    fromDateTime = LocalDate.of(year.value - 1, Month.DECEMBER, 31).atTime(LocalTime.MAX),
                    toDateTime = LocalDate.of(year.value + 1, Month.JANUARY, 1).atStartOfDay(),
                )
        }

        val saksdataFields = mapToFields(saksdataList)

        val workbook = XSSFWorkbook()

        val sheet = workbook.createSheet("Statistikk år $year")

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

        saksdataFields.forEach { saksdataRow ->
            val row = sheet.createRow(rowCounter++)

            var columnCounter = 0

            saksdataRow.forEach { column ->
                val style = workbook.createCellStyle()
                style.setFont(cellFont)
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

    private fun mapToFields(saksdataList: List<Saksdata>): List<List<Field>> {
        //@formatter:off
        return saksdataList.map { saksdata ->
            buildList {
                //Saksdata
                add(Field(fieldName = "Tilknyttet enhet", value = saksdata.tilknyttetEnhet.toEnhetnummer(), type = STRING))
                add(Field(fieldName = "Sakstype", value = saksdata.sakstype.navn, type = STRING))
                add(Field(fieldName = "Ytelse", value = saksdata.ytelse!!.navn, type = STRING))
                add(Field(fieldName = "Mottatt vedtaksinstans", value = saksdata.mottattVedtaksinstans, type = DATE))
                add(Field(fieldName = "Mottatt klageinstans", value = saksdata.mottattKlageinstans, type = DATE))
                add(Field(fieldName = "Fra vedtaksenhet", value = saksdata.vedtaksinstansEnhet.toEnhetnummer(), type = STRING))
                add(Field(fieldName = "Utfall/Resultat", value = saksdata.utfall!!.navn, type = STRING))
                add(Field(fieldName = "Hjemmel", value = saksdata.registreringshjemler.toHjemlerString(), type = STRING))

                //Klageforberedelsen
                add(Field(fieldName = "Klageforberedelsen", value = saksdata.kvalitetsvurdering.klageforberedelsenRadioValg, type = STRING))
                add(Field(fieldName = "Sakens dokumenter", value = saksdata.kvalitetsvurdering.sakensDokumenter, type = BOOLEAN))
                add(Field(fieldName = "Oversittet klagefrist er ikke kommentert", value = saksdata.kvalitetsvurdering.oversittetKlagefristIkkeKommentert, type = BOOLEAN))
                add(Field(fieldName = "Klagerens relevante anførseler er ikke tilstrekkelig kommentert/imøtegått", value = saksdata.kvalitetsvurdering.klagerensRelevanteAnfoerslerIkkeKommentert, type = BOOLEAN))
                add(Field(fieldName = "Begrunnelse for hvorfor avslag opprettholdes / klager ikke oppfyller vilkår", value = saksdata.kvalitetsvurdering.begrunnelseForHvorforAvslagOpprettholdes, type = BOOLEAN))
                add(Field(fieldName = "Konklusjonen", value = saksdata.kvalitetsvurdering.konklusjonen, type = BOOLEAN))
                add(Field(fieldName = "Oversendelsesbrevets innhold er ikke i samsvar med sakens tema", value = saksdata.kvalitetsvurdering.oversendelsesbrevetsInnholdIkkeISamsvarMedTema, type = BOOLEAN))

                //Utredningen
                add(Field(fieldName = "Utredningen", value = saksdata.kvalitetsvurdering.utredningenRadioValg, type = STRING))
                add(Field(fieldName = "Utredningen av medisinske forhold", value = saksdata.kvalitetsvurdering.utredningenAvMedisinskeForhold, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av medisinske forhold stikkord", value = saksdata.kvalitetsvurdering.utredningenAvMedisinskeForholdText, type = STRING))
                add(Field(fieldName = "Utredningen av inntektsforhold", value = saksdata.kvalitetsvurdering.utredningenAvInntektsforhold, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av inntektsforhold stikkord", value = saksdata.kvalitetsvurdering.utredningenAvInntektsforholdText, type = STRING))
                add(Field(fieldName = "Utredningen av arbeid", value = saksdata.kvalitetsvurdering.utredningenAvArbeid, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av arbeid stikkord", value = saksdata.kvalitetsvurdering.utredningenAvArbeidText, type = STRING))
                add(Field(fieldName = "Arbeidsrettet brukeroppfølging", value = saksdata.kvalitetsvurdering.arbeidsrettetBrukeroppfoelging, type = BOOLEAN))
                add(Field(fieldName = "Arbeidsrettet brukeroppfølging stikkord", value = saksdata.kvalitetsvurdering.arbeidsrettetBrukeroppfoelgingText, type = STRING))
                add(Field(fieldName = "Utredningen av andre aktuelle forhold i saken", value = saksdata.kvalitetsvurdering.utredningenAvAndreAktuelleForholdISaken, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av andre aktuelle forhold i saken stikkord", value = saksdata.kvalitetsvurdering.utredningenAvAndreAktuelleForholdISakenText, type = STRING))
                add(Field(fieldName = "Utredningen av EØS / utenlandsproblematikk", value = saksdata.kvalitetsvurdering.utredningenAvEoesProblematikk, type = BOOLEAN))
                add(Field(fieldName = "Utredningen av EØS / utenlandsproblematikk stikkord", value = saksdata.kvalitetsvurdering.utredningenAvEoesProblematikkText, type = STRING))
                add(Field(fieldName = "Veiledning fra NAV", value = saksdata.kvalitetsvurdering.veiledningFraNav, type = BOOLEAN))
                add(Field(fieldName = "Veiledning fra NAV stikkord", value = saksdata.kvalitetsvurdering.veiledningFraNavText, type = STRING))

                //Vedtaket
                add(Field(fieldName = "Vedtaket", value = saksdata.kvalitetsvurdering.vedtaketRadioValg, type = STRING))
                add(Field(fieldName = "Det er ikke brukt riktig hjemmel(er)", value = saksdata.kvalitetsvurdering.detErIkkeBruktRiktigHjemmel, type = BOOLEAN))
                add(Field(fieldName = "Innholdet i rettsreglene er ikke tilstrekkelig beskrevet", value = saksdata.kvalitetsvurdering.innholdetIRettsregleneErIkkeTilstrekkeligBeskrevet, type = BOOLEAN))
                add(Field(fieldName = "Rettsregelen er benyttet eller tolket feil", value = saksdata.kvalitetsvurdering.rettsregelenErBenyttetFeil, type = BOOLEAN))
                add(Field(fieldName = "Vurdering av faktum / bevisvurdering er mangelfull", value = saksdata.kvalitetsvurdering.vurderingAvFaktumErMangelfull, type = BOOLEAN))
                add(Field(fieldName = "Det er feil i den konkrete rettsanvendelsen", value = saksdata.kvalitetsvurdering.detErFeilIKonkretRettsanvendelse, type = BOOLEAN))
                add(Field(fieldName = "Begrunnelsen er ikke konkret og individuell", value = saksdata.kvalitetsvurdering.begrunnelsenErIkkeKonkretOgIndividuell, type = BOOLEAN))
                add(Field(fieldName = "Språket/Formidlingen er ikke tydelig", value = saksdata.kvalitetsvurdering.spraaketErIkkeTydelig, type = BOOLEAN))

                //Annet
                add(Field(fieldName = "Nye opplysninger mottatt etter oversendelse til klageinstansen", value = saksdata.kvalitetsvurdering.nyeOpplysningerMottatt, type = BOOLEAN))
                add(Field(fieldName = "Bruk gjerne vedtaket som eksempel i opplæring", value = saksdata.kvalitetsvurdering.brukIOpplaering, type = BOOLEAN))
                add(Field(fieldName = "Bruk gjerne vedtaket som eksempel i opplæring stikkord", value = saksdata.kvalitetsvurdering.brukIOpplaeringText, type = STRING))

                //ROL
                add(Field(fieldName = "Bruk av rådgivende lege", value = saksdata.kvalitetsvurdering.brukAvRaadgivendeLegeRadioValg, type = STRING))
                add(Field(fieldName = "Rådgivende lege er ikke brukt", value = saksdata.kvalitetsvurdering.raadgivendeLegeErIkkeBrukt, type = BOOLEAN))
                add(Field(fieldName = "Rådgivende lege er brukt, men saksbehandler har stilt feil spørsmål og får derfor feil svar", value = saksdata.kvalitetsvurdering.raadgivendeLegeErBruktFeilSpoersmaal, type = BOOLEAN))
                add(Field(fieldName = "Rådgivende lege har uttalt seg om tema utover trygdemedisin", value = saksdata.kvalitetsvurdering.raadgivendeLegeHarUttaltSegUtoverTrygdemedisin, type = BOOLEAN))
                add(Field(fieldName = "Rådgivende lege er brukt, men dokumentasjonen er mangelfull / ikke skriftliggjort", value = saksdata.kvalitetsvurdering.raadgivendeLegeErBruktMangelfullDokumentasjon, type = BOOLEAN))

            //@formatter:on
            }
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

}