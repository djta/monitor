insert into ${schema}s_service
(
	service_id,
	path,
	description,
	description_cn,
	description_en,
	app_id,
	group_id
)
select 

	${seq:nextval@${schema}seq_service},
	${fld:path},
	${fld:description},
	${fld:description_cn},
	${fld:description_en},
	app_id,
	group_id
from
	${schema}s_service_group
where
	group_id = ${fld:group_id}
