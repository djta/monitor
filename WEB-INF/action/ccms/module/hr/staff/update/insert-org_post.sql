insert into hr_post_staff
(
	tuid,
	org_post_id,
	userlogin,
	created,
	createdby
)
values 
(
	${seq:nextval@seq_hr_post_staff},
	${fld:org_post_id},
	${fld:userlogin},
	'${def:timestamp}',
	${fld:userlogin}
)
