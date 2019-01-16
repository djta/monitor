delete from
	${schema}s_service_group_role
where 
	group_id in(select group_id from ${schema}s_service_group where app_id = ${fld:id})
