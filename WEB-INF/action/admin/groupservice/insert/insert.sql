insert into ${schema}s_service_group
(
	group_id,
	app_id,
	group_name
)
values 
(
	${seq:nextval@${schema}seq_service_group},
	${fld:app_id},
	${fld:group_name}
)
