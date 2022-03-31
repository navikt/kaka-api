package no.nav.klage.kaka.domain.kodeverk

enum class Role {
    ROLE_KAKA_KVALITETSVURDERING,
    ROLE_KAKA_KVALITETSTILBAKEMELDINGER,
    ROLE_KAKA_TOTALSTATISTIKK,
    ROLE_KAKA_LEDERSTATISTIKK,

    ROLE_KLAGE_EGEN_ANSATT,
    ROLE_KLAGE_FORTROLIG,
    ROLE_KLAGE_STRENGT_FORTROLIG,

    ROLE_ADMIN,

    //Old roles
    ROLE_KLAGE_LEDER,
    ROLE_KAKA_SAKSBEHANDLER,
    ROLE_VEDTAKSINSTANS_LEDER
}