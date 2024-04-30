UPDATE kaka.registreringshjemmel
SET id = '628'
WHERE id = '627'
  AND saksdata_id NOT IN (SELECT saksdata_id
                          from kaka.registreringshjemmel
                          WHERE id = '628');