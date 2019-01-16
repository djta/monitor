insert into ${schema}s_user_role
(
	user_role_id,
	user_id,
	role_id
)
values 
(
	${seq:nextval@${schema}seq_user_role},
	${fld:user_id},
	${fld:role_id}
)
