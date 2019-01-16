INSERT INTO cc_message(
	tuid,
	issystem,
	templateid,
	recuser,
	sendtime,
	recusername,
	content,
	status,
	remind,
	org_id
) VALUES (
	${seq:nextval@seq_cc_message},
	1,
	NULL,
	${fld:mc},
	{ts'${def:timestamp}'},
	(SELECT name FROM hr_staff WHERE userlogin = ${fld:mc} LIMIT 1),
	concat('您的资源',COALESCE(${fld:name},''),'将在',${fld:outdate},'过期，请尽快跟进'),
	1,
	1,
	${fld:org_id}
)