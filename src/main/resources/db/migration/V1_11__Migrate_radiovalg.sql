UPDATE kaka.kvalitetsvurdering
SET klageforberedelsen_radio_valg = 'BRA'
WHERE klageforberedelsen_radio_valg = '0';

UPDATE kaka.kvalitetsvurdering
SET klageforberedelsen_radio_valg = 'MANGELFULLT'
WHERE klageforberedelsen_radio_valg = '1';

UPDATE kaka.kvalitetsvurdering
SET utredningen_radio_valg = 'BRA'
WHERE utredningen_radio_valg = '0';

UPDATE kaka.kvalitetsvurdering
SET utredningen_radio_valg = 'MANGELFULLT'
WHERE utredningen_radio_valg = '1';

UPDATE kaka.kvalitetsvurdering
SET vedtaket_radio_valg = 'BRA'
WHERE vedtaket_radio_valg = '0';

UPDATE kaka.kvalitetsvurdering
SET vedtaket_radio_valg = 'MANGELFULLT'
WHERE vedtaket_radio_valg = '1';

UPDATE kaka.kvalitetsvurdering
SET bruk_av_raadgivende_lege_radio_valg = 'IKKE_AKTUELT'
WHERE bruk_av_raadgivende_lege_radio_valg = '0';

UPDATE kaka.kvalitetsvurdering
SET bruk_av_raadgivende_lege_radio_valg = 'BRA'
WHERE bruk_av_raadgivende_lege_radio_valg = '1';

UPDATE kaka.kvalitetsvurdering
SET bruk_av_raadgivende_lege_radio_valg = 'MANGELFULLT'
WHERE bruk_av_raadgivende_lege_radio_valg = '2';

ALTER TABLE kaka.kvalitetsvurdering
    RENAME TO kvalitetsvurdering_v1;
