UPDATE kaka.registreringshjemmel
SET id = 'FTRL_17_5_1'
WHERE saksdata_id = 'c180d158-8b4b-421b-a2e1-59603060344e'
AND id = '555';

UPDATE kaka.saksdata
SET ytelse_id = '52'
WHERE kvalitetsvurdering_id IN(
                               '65b03d06-6fe1-4000-b404-a63b47d91521',
                               'b714c320-eed2-471f-a3c4-46eb97d7b647',
                               'e2b2e3d7-1341-4040-82a2-0dd0b09deadb',
                               '5bac0fb3-584a-49dc-b4f4-c63c65ffdbb1',
                               '7c23f134-594e-4405-a5db-105c1a631c71',
                               'db924ec3-10aa-4060-9066-1ebf923b10a6'
    )
AND ytelse_id = '30';