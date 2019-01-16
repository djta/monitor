insert into cc_message
(   
    tuid,
	senduser,
    sendusername,
	recuser,
	recusername,
	content,
	sendtime,
	status,
	remind,
	org_id
)
values
(
	${seq:nextval@seq_cc_message},
	'sys',
	'sys',
	${fld:customercode},
	${fld:cust_name},
	'健身区域前台离场',
	{ts'${def:timestamp}'},
	1,
	0,
	${fld:org_id}
)







