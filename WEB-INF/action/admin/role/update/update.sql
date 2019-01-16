update 
	${schema}s_role 
set 
	app_id = ${fld:app_id},
	rolename = ${fld:rolename},
	description = ${fld:description}
where
	role_id = ${fld:tuid}
