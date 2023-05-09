UPDATE kaka.registreringshjemmel r
SET id = 'BVL_15_2'
FROM kaka.saksdata s
WHERE s.id = r.saksdata_id
  AND r.id = '843'
  AND s.kvalitetsvurdering_version = '2';

UPDATE kaka.registreringshjemmel r
SET id = 'BVL_15_2A'
FROM kaka.saksdata s
WHERE s.id = r.saksdata_id
  AND r.id = '693'
  AND s.kvalitetsvurdering_version = '2';

UPDATE kaka.registreringshjemmel r
SET id = 'BVL_15_2B'
FROM kaka.saksdata s
WHERE s.id = r.saksdata_id
  AND r.id = '694'
  AND s.kvalitetsvurdering_version = '2';

UPDATE kaka.saksdata
SET vedtaksinstans_enhet = '4833'
WHERE kvalitetsvurdering_version = '2'
  AND vedtaksinstans_enhet = '4802';

UPDATE kaka.registreringshjemmel
SET id = 'BL_75B'
WHERE id = '639';

