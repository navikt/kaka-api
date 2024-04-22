UPDATE kaka.saksdata
SET vedtaksinstans_enhet = '4487'
WHERE vedtaksinstans_enhet IN ('4432', '4409')
  AND ytelse_id IN ('1', '2', '3', '4')
  AND dato_saksdata_avsluttet_av_saksbehandler > '2021-06-28';