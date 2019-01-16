insert into ${schema}s_application  
(
	app_id,
	app_alias,
	description,
	pwd_policy,
	created,
	createdby
)
values 
(
	${seq:nextval@${schema}seq_application},
	${fld:app_alias},
	${fld:description},
	${fld:pwd_policy},
	{ts '${def:timestamp}'},
	'${def:user}'
)
