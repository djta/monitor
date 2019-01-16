INSERT	INTO
hr_authority_group
(
	tuid
	, group_name
	, remark
	, created
	, createdby
	,tenantry_id 
)
VALUES
(
	${seq:nextval@seq_hr_authority_group}
	,${fld:group_name}
	,${fld:remark}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,${def:tenantry}
)