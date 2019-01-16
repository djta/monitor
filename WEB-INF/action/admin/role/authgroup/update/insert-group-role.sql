insert into ${schema}s_service_group_role
(
	group_role_id,
	group_id,
	role_id
)
values 
(
	${seq:nextval@${schema}seq_service_group_role},
	${fld:group_id},
	${fld:role_id}
)
