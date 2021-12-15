package no.nav.klage.kaka.clients.azure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class AzureUser(
    val onPremisesSamAccountName: String,
    val displayName: String,
    val givenName: String,
    val surname: String,
    val mail: String,
    val officeLocation: String?,
    val userPrincipalName: String,
    val id: String,
    val jobTitle: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AzureSlimUser(
    val userPrincipalName: String,
    val onPremisesSamAccountName: String,
    val displayName: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AzureGroupMember(
    val id: String,
    val mail: String,
    val onPremisesSamAccountName: String,
    val displayName: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AzureGroup(
    val id: String,
    val displayName: String?,
    val mailNickname: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AzureUserList(val value: List<AzureUser>?)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AzureSlimUserList(val value: List<AzureSlimUser>?)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AzureGroupMemberList(val value: List<AzureGroupMember>?)

@JsonIgnoreProperties(ignoreUnknown = true)
data class AzureGroupList(val value: List<AzureGroup>)

