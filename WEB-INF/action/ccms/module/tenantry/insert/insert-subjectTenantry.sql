INSERT	INTO
t_subject_tenantry
(
	tuid
	,subject_id
	,tenantry_id
	,created
	,createdby
	,is_default
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,${fld:subject_id}
	,${seq:currval@seq_tenantry}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,(case when ${fld:is_default}=${fld:subject_id} then 1 else 0 end)
)