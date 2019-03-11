insert into cc_device
(
	tuid,
	name,
	device_num,
	ip,
	c_type,
	p_type,
	port,
	username,
	password,
	created,
	createdby
)
values 
(
	${seq:nextval@seq_cc_device},
	${fld:name},
	${fld:device_num},
	${fld:ip},
	${fld:c_type},
	'0',
	${fld:port},
	${fld:username},
	${fld:password},
	{ts '${def:timestamp}'},
	'${def:user}'
)
