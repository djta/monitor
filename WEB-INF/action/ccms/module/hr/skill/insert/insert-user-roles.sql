insert into ${schema}s_user_role
(
	user_role_id,
	user_id,
	role_id
)
select
	${seq:nextval@${schema}seq_user_role},
	s.user_id,
	${fld:role_id}
from
	hr_staff s
	inner join hr_staff_skill ss on s.user_id=ss.user_id
where
	ss.skill_id = ${fld:tuid}