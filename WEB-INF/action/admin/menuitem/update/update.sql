update 
	${schema}s_service 
set 
	description_cn = ${fld:description_cn},
	description_en = ${fld:description_en}

where
	service_id = ${fld:service_id}