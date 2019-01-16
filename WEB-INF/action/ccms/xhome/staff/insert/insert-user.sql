insert into ${schema}s_user(
	user_id,
	userlogin,
	passwd,
	fname,
	email,
	enabled,
	pwd_policy,
	force_newpass,
	locale
) values(
	${seq:currval@${schema}seq_user},
	${fld:userlogin},
	${fld:passwd},
	${fld:name},
	NULL,
	1,
	-2,
	NULL,
	'cn'
)