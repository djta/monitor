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
	current_timestamp,
	${fld:passwd},
	cast(${seq:currval@${schema}seq_user} as int)
)
