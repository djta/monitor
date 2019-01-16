DELETE FROM hr_staff_skill WHERE user_id = ${fld:user_id};

insert into hr_staff_skill(
	tuid,
	user_id,
	userlogin,
	skill_id,
	created,
	createdby
) values(
	${seq:nextval@seq_hr_staff_skill},
	${fld:user_id},
	(SELECT userlogin FROM hr_staff WHERE user_id = ${fld:user_id}),
	${fld:skill_id},
	{ts '${def:timestamp}'},
	'${def:user}'
)