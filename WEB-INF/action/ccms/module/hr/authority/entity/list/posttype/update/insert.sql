INSERT	INTO
hr_authority_list
(
	tuid
	, entity_id
	, entity_value
	, created
	, createdby
)
VALUES
(
	${seq:nextval@seq_hr_authority_list}
	,${fld:entity_id}
	,${fld:post_id}
	,{ts '${def:timestamp}'}
	,'${def:user}'
)