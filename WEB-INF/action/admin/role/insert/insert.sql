insert into ${schema}s_role  
(
	role_id,
	app_id,
	rolename,
	description
)
values 
(
	${seq:nextval@${schema}seq_role},
	${fld:app_id},
	${fld:rolename},
	${fld:description}
)
