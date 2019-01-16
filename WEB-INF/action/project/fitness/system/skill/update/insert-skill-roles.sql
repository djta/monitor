insert into hr_skill_role
(
	tuid,
	skill_id,
	role_id
)
values 
(
	${seq:nextval@seq_hr_skill_role},
	${fld:tuid},
	${fld:role_id}
)
