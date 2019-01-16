delete from 
	${schema}s_panel 
where 
	service_id 
in (select service_id from ${schema}s_service where group_id = ${fld:id})
