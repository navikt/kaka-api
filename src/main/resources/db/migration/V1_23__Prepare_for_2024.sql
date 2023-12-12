ALTER TABLE kaka.kvalitetsvurdering_v2
    ADD COLUMN klageforberedelsen_uuk BOOLEAN default false,
    ADD COLUMN klageforberedelsen_uuk_khbuoaino BOOLEAN default false,
    ADD COLUMN klageforberedelsen_uuk_khbuoaino_fritekst TEXT,
    ADD COLUMN klageforberedelsen_uuk_khsino BOOLEAN default false,
    ADD COLUMN klageforberedelsen_uuk_khsino_fritekst TEXT,
    ADD COLUMN utredningen_av_sivilstand_boforhold BOOLEAN default false,
    ADD COLUMN vedtaket_brukt_feil_hjemmel BOOLEAN default false,
    ADD COLUMN vedtaket_alle_relevante_hjemler_er_ikke_vurdert BOOLEAN default false;


create table kaka.r_k_v2_vedtaket_bfh_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v2_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v2_id),
    CONSTRAINT fk_r_k_v2_vedtaket_bfh_hjemler_list_kv_v2
        FOREIGN KEY (kvalitetsvurdering_v2_id)
            REFERENCES kaka.kvalitetsvurdering_v2 (id)
);


create table kaka.r_k_v2_vedtaket_arheiv_hjemler_list
(
    id                       TEXT NOT NULL,
    kvalitetsvurdering_v2_id UUID NOT NULL,
    PRIMARY KEY (id, kvalitetsvurdering_v2_id),
    CONSTRAINT fk_r_k_v2_vedtaket_arheiv_hjemler_list_kv_v2
        FOREIGN KEY (kvalitetsvurdering_v2_id)
            REFERENCES kaka.kvalitetsvurdering_v2 (id)
);
