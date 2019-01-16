INSERT	INTO
hr_authority_entity
(
	tuid
	, authority_id
	, entity_name
	, entity_type
	, remark
	, created
	, createdby
)
VALUES
(
	${seq:nextval@seq_hr_authority_entity}
	,${fld:authority_id}
	,${fld:entity_name}
	,${fld:entity_type}
	,${fld:remark}
	,{ts '${def:timestamp}'}
	,'${def:user}'
)