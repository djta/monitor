insert into cc_register(
	tuid,
	name,
	mobile,
	company_name,
	remark,
	status,
	createdby,
	created,
	user_id,
	userlogin,
	code
) values(
	${seq:nextval@seq_cc_register},
	${fld:name},
	${fld:mobile},
	${fld:company_name},
	NULL,
	1,
	NULL,
	{ts '${def:timestamp}'},
	${user_id},
	'${userlogin}',
	'${passwd}'
)
