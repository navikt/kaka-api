package no.nav.klage.kaka.services


import no.nav.klage.kaka.util.TokenUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class InnloggetSaksbehandlerService(
    private val tokenUtil: TokenUtil,
    @Value("\${ROLE_GOSYS_OPPGAVE_BEHANDLER}") private val gosysSaksbehandlerRole: String,
    @Value("\${ROLE_KLAGE_SAKSBEHANDLER}") private val saksbehandlerRole: String,
    @Value("\${ROLE_KLAGE_FAGANSVARLIG}") private val fagansvarligRole: String,
    @Value("\${ROLE_KLAGE_LEDER}") private val lederRole: String,
    @Value("\${ROLE_KLAGE_MERKANTIL}") private val merkantilRole: String,
    @Value("\${ROLE_KLAGE_FORTROLIG}") private val kanBehandleFortroligRole: String,
    @Value("\${ROLE_KLAGE_STRENGT_FORTROLIG}") private val kanBehandleStrengtFortroligRole: String,
    @Value("\${ROLE_KLAGE_EGEN_ANSATT}") private val kanBehandleEgenAnsattRole: String,
    @Value("\${ROLE_ADMIN}") private val adminRole: String
) {
    fun getInnloggetIdent() = tokenUtil.getIdent()

    fun isAdmin(): Boolean = tokenUtil.getRollerFromToken().hasRole(adminRole)

    fun isLeder(): Boolean = tokenUtil.getRollerFromToken().hasRole(lederRole)

    fun isFagansvarlig(): Boolean = tokenUtil.getRollerFromToken().hasRole(fagansvarligRole)

    fun isSaksbehandler(): Boolean =
        tokenUtil.getRollerFromToken().hasRole(saksbehandlerRole) || tokenUtil.getRollerFromToken()
            .hasRole(gosysSaksbehandlerRole)

    private fun List<String>.hasRole(role: String) = any { it.contains(role) }
}