update 
	${schema}s_service_group 
set 
	group_name = ${fld:group_name},
	app_id = ${fld:app_id}
where
	group_id = ${fld:tuid}
