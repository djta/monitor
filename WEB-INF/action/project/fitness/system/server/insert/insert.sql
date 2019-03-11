insert into cc_server
(
	tuid,
	ip,
	type,
	port,
	username,
	password,
	created,
	createdby
)
values 
(
	${seq:nextval@seq_cc_server},
	${fld:ip},
	${fld:type},
	${fld:port},
	${fld:username},
	${fld:password},
	{ts '${def:timestamp}'},
	'${def:user}'
)
