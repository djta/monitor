insert into hr_staff_skill
(
	tuid,
	user_id,
	skill_id,
	userlogin
)
values 
(
	${seq:nextval@${schema}seq_default},
	${seq:currval@${schema}seq_user},
	${fld:skill_id},
	${fld:userlogin}
)
