INSERT INTO
	hr_grant_log
(
	tuid
	,userlogin
	,authuser
	,op_title
	,op_action
	,created
	,createdby
	,grant_id
)
VALUES
(
	 ${seq:nextval@seq_hr_grant_log}
	,${fld:userlogin}
	,${fld:authuser}
	,${fld:op_title}
	,${fld:op_action}
	, {ts '${def:timestamp}'}
	,'${def:user}'
	,(select max(tuid) from hr_grant where userlogin=${fld:userlogin} and authuser=${fld:authuser} and status='0')
)
