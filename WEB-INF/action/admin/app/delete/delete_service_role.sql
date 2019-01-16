delete from
	${schema}s_service_role
where 
	role_id in(select role_id from ${schema}s_role where app_id = ${fld:id})
