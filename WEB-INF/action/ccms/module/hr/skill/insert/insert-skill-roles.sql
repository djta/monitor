insert into hr_skill_role
(
	tuid,
	skill_id,
	role_id,
	created,
	createdby
)
values 
(
	${seq:nextval@seq_hr_skill_role},
	${seq:currval@seq_hr_skill},
	${fld:role_id},
	{ts '${def:timestamp}'},
	'${def:user}'
)