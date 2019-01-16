insert into ${schema}s_service_role
(
	service_role_id,
	service_id,
	role_id
)
select
	${seq:nextval@${schema}seq_service_role},
	service_id,
	${fld:role_id}
from
	${schema}s_service
where
	group_id = ${fld:group_id}
