update 
	${schema}s_service 
set 
	path = ${fld:path},
	description = ${fld:description},
	description_cn = ${fld:description_cn},
	description_en = ${fld:description_en}
where
	service_id = ${fld:tuid}
