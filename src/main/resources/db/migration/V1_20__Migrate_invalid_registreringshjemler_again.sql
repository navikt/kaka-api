UPDATE kaka.registreringshjemmel
SET id = 'FTRL_10_6E'
WHERE id = '10'
  AND saksdata_id = 'ca240611-2a1a-405e-ad5a-30d4b8fcdac0';

UPDATE kaka.registreringshjemmel
SET id = 'FTRL_4_16C'
WHERE id = '248'
  AND saksdata_id = '40e0ed82-a42c-4ac4-8c90-2f6e98f44334';

UPDATE kaka.registreringshjemmel
SET id = 'FTRL_14_7A'
WHERE id = '403'
  AND saksdata_id in
  (
   '17b7c16f-b50e-448b-9d9a-ebe05b81b81e',
   '3a5c2d1e-723a-4686-9360-b0fd19183df4',
   '69035d13-54c7-4dfb-a94c-12f4d422b6e9',
   'bd840aa9-955c-4f69-8c1c-61dd9f389ebc',
   'd7f88ac7-707a-4850-bd1c-daf66532be74',
   );

DELETE
FROM kaka.registreringshjemmel
WHERE id = '190'
  AND saksdata_id = '3a5c2d1e-723a-4686-9360-b0fd19183df4';

DELETE
FROM kaka.registreringshjemmel
WHERE id in ('151', '152')
  AND saksdata_id = '16b00284-76ff-4676-84d0-f3e57a8469f8';

DELETE
FROM kaka.registreringshjemmel
WHERE id in ('195')
  AND saksdata_id = 'df0b4abf-19c2-4964-a419-28ff875d6adf';

DELETE
FROM kaka.registreringshjemmel
WHERE id in ('219')
  AND saksdata_id = '301451b9-fa16-46fe-abd0-e26ae9c9f990';

DELETE
FROM kaka.registreringshjemmel
WHERE id in ('415')
  AND saksdata_id = 'bd840aa9-955c-4f69-8c1c-61dd9f389ebc';

DELETE
FROM kaka.registreringshjemmel
WHERE id in ('415')
  AND saksdata_id = 'df0b4abf-19c2-4964-a419-28ff875d6adf';

DELETE
FROM kaka.registreringshjemmel
WHERE id in ('427')
  AND saksdata_id = '0ba2c4c0-ca3a-452e-983a-120b19bf3742';

UPDATE kaka.registreringshjemmel
SET id = 'FTRL_17_8A'
WHERE id = '558'
  AND saksdata_id = '085367f5-7234-45b3-b038-99177766e22c';

UPDATE kaka.registreringshjemmel
SET id = 'FTRL_17_8B'
WHERE id = '558'
  AND saksdata_id = '62b5547f-9932-4c28-9928-0854749a16e1';