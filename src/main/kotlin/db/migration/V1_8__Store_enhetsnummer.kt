package db.migration

import no.nav.klage.kodeverk.Enhet
import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import java.util.*


class V1_8__Store_enhetsnummer: BaseJavaMigration() {
    override fun migrate(context: Context) {
        val preparedStatement = context.connection.prepareStatement(
            """
                UPDATE kaka.saksdata 
                    SET tilknyttet_enhet = ?,
                        vedtaksinstans_enhet = ?
                WHERE id = ?
            """.trimIndent()
        )

        context.connection.createStatement().use { select ->
            select.executeQuery("SELECT id, tilknyttet_enhet, vedtaksinstans_enhet FROM kaka.saksdata").use { rows ->
                while (rows.next()) {
                    val id = rows.getObject(1, UUID::class.java)
                    val tilknyttetEnhetId = rows.getString(2)
                    val vedtaksinstansEnhetId = rows.getString(3)

                    val tilknyttetEnhet = Enhet.values().find { it.id == tilknyttetEnhetId }
                    val vedtaksinstansEnhet = Enhet.values().find { it.id == vedtaksinstansEnhetId }

                    preparedStatement.setString(1, tilknyttetEnhet?.navn)
                    preparedStatement.setString(2, vedtaksinstansEnhet?.navn)
                    preparedStatement.setObject(3, id)

                    preparedStatement.executeUpdate()
                }
            }
        }
    }
}