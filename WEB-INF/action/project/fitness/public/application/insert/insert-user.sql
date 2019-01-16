insert into ${schema}s_user (
	user_id,
	userlogin,
	passwd,
	fname,
	enabled,
	pwd_policy,
	force_newpass,
	locale
) values(
	${user_id},
	'${userlogin}',
	'${passwd}',
	${fld:name},
	1,
	-2,
	null,
	'cn'
)