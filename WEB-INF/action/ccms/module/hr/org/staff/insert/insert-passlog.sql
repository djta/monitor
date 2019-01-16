insert into ${schema}s_passlog  
(
	passlog_id,
	last_change,
	hash,
	user_id
)
values 
(
	cast(${seq:nextval@${schema}seq_passlog} as int),
	{d '${def:date}'},
	${fld:passwd},
	cast(${seq:currval@${schema}seq_user} as int)
)
