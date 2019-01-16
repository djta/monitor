insert into ${schema}s_service_role
(
	service_role_id,
	service_id,
	role_id
)
values 
(
	${seq:nextval@${schema}seq_service_role},
	${fld:service_id},
	${fld:role_id}
)
