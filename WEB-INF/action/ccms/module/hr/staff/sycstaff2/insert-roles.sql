insert into ${schema}s_user_role
(
	user_role_id,
	user_id,
	role_id
)
select
	${seq:nextval@${schema}seq_user_role},
	${fld:user_id},
	s.role_id
from
	hr_skill_role s

where
	s.skill_id = ${fld:skill_id}