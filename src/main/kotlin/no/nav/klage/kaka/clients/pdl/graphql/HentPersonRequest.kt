package no.nav.klage.kaka.clients.pdl.graphql

data class PersonGraphqlQuery(
    val query: String,
    val variables: FnrVariables
)

data class FnrVariables(
    val ident: String
)

fun hentPersonQuery(fnr: String): PersonGraphqlQuery {
    val query =
        PersonGraphqlQuery::class.java.getResource("/pdl/hentPerson.graphql").cleanForGraphql()
    return PersonGraphqlQuery(query, FnrVariables(fnr))
}
