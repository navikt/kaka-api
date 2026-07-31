DELETE
FROM kaka.registreringshjemmel
WHERE id = 'FTRL_4_16A'
  AND saksdata_id IN (SELECT saksdata_id
                  FROM kaka.registreringshjemmel
                  WHERE id = '864');

UPDATE kaka.registreringshjemmel
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_brukt_feil_
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v2_id IN (SELECT kvalitetsvurdering_v2_id
                  FROM kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_brukt_feil_
                  WHERE id = '864');

UPDATE kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_brukt_feil_
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_feil_konkre
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v2_id IN (SELECT kvalitetsvurdering_v2_id
                  FROM kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_feil_konkre
                  WHERE id = '864');

UPDATE kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_feil_konkre
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_innholdet_i
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v2_id IN (SELECT kvalitetsvurdering_v2_id
                  FROM kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_innholdet_i
                  WHERE id = '864');

UPDATE kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_innholdet_i
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_lovbestemme
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v2_id IN (SELECT kvalitetsvurdering_v2_id
                  FROM kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_lovbestemme
                  WHERE id = '864');

UPDATE kaka.registreringshjemmel_kvalitetsvurdering_v2_vedtaket_lovbestemme
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.r_k_v2_vedtaket_arheiv_hjemler_list
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v2_id IN (SELECT kvalitetsvurdering_v2_id
                  FROM kaka.r_k_v2_vedtaket_arheiv_hjemler_list
                  WHERE id = '864');

UPDATE kaka.r_k_v2_vedtaket_arheiv_hjemler_list
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.r_k_v2_vedtaket_bfh_hjemler_list
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v2_id IN (SELECT kvalitetsvurdering_v2_id
                  FROM kaka.r_k_v2_vedtaket_bfh_hjemler_list
                  WHERE id = '864');

UPDATE kaka.r_k_v2_vedtaket_bfh_hjemler_list
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.r_k_v3_saerregelverk_deltgff_hjemler_list
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v3_id IN (SELECT kvalitetsvurdering_v3_id
                  FROM kaka.r_k_v3_saerregelverk_deltgff_hjemler_list
                  WHERE id = '864');

UPDATE kaka.r_k_v3_saerregelverk_deltgff_hjemler_list
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.r_k_v3_saerregelverk_vbpfh_el_lt_hjemler_list
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v3_id IN (SELECT kvalitetsvurdering_v3_id
                  FROM kaka.r_k_v3_saerregelverk_vbpfh_el_lt_hjemler_list
                  WHERE id = '864');

UPDATE kaka.r_k_v3_saerregelverk_vbpfh_el_lt_hjemler_list
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.r_k_v3_saerregelverk_vbpfkr_hjemler_list
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v3_id IN (SELECT kvalitetsvurdering_v3_id
                  FROM kaka.r_k_v3_saerregelverk_vbpfkr_hjemler_list
                  WHERE id = '864');

UPDATE kaka.r_k_v3_saerregelverk_vbpfkr_hjemler_list
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.r_k_v3_sbr_begrunnelsesplikten_bnif_hjemler_list
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v3_id IN (SELECT kvalitetsvurdering_v3_id
                  FROM kaka.r_k_v3_sbr_begrunnelsesplikten_bnif_hjemler_list
                  WHERE id = '864');

UPDATE kaka.r_k_v3_sbr_begrunnelsesplikten_bnif_hjemler_list
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.r_k_v3_sbr_begrunnelsesplikten_bnih_hjemler_list
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v3_id IN (SELECT kvalitetsvurdering_v3_id
                  FROM kaka.r_k_v3_sbr_begrunnelsesplikten_bnih_hjemler_list
                  WHERE id = '864');

UPDATE kaka.r_k_v3_sbr_begrunnelsesplikten_bnih_hjemler_list
SET id = '864'
WHERE id = 'FTRL_4_16A';

DELETE
FROM kaka.r_k_v3_sbr_begrunnelsesplikten_bvitr_hjemler_list
WHERE id = 'FTRL_4_16A'
  AND kvalitetsvurdering_v3_id IN (SELECT kvalitetsvurdering_v3_id
                  FROM kaka.r_k_v3_sbr_begrunnelsesplikten_bvitr_hjemler_list
                  WHERE id = '864');

UPDATE kaka.r_k_v3_sbr_begrunnelsesplikten_bvitr_hjemler_list
SET id = '864'
WHERE id = 'FTRL_4_16A';
