insert into hr_staff_skill(
	tuid,
	user_id,
	skill_id,
	userlogin
) values (
	${seq:nextval@${schema}seq_default},
	${user_id},
	(
		select skill_id from hr_skill where org_id = (
			select min(org_id) from hr_org
		) and skill_name = '店长' limit 1
	),
	'${userlogin}'
)
