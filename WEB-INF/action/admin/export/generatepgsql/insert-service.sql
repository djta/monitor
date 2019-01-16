insert into ${schema}s_service
(
	service_id,
	path,
	description,
	app_id
)
values 
(
	${seq:nextval@${schema}seq_service},
	${fld:path},
	${fld:description},
	${seq:currval@${schema}seq_application}
);


