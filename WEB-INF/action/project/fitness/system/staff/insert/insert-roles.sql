insert into ${schema}s_user_role
(
	user_role_id,
	user_id,
	role_id
)
select
	${seq:nextval@${schema}seq_user_role},
	${seq:currval@${schema}seq_user},
	s.role_id
from security.s_role s
where s.role_id = 3	/*login*/

