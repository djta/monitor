INSERT	INTO
hr_authority
(
	tuid
	, authority_name
	, remark
	, created
	, createdby
	,tenantry_id
)
VALUES
(
	${seq:nextval@seq_hr_authority}
	,${fld:authority_name}
	,${fld:remark}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,${def:tenantry}
)