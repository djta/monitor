INSERT	INTO
hr_authority_relation
(
	tuid
	, group_id
	, authority_id
	, access_type
	, logic_type
	, created
	, createdby
)
VALUES
(
	${seq:nextval@seq_hr_authority_relation}
	,${fld:group_id}
	,${fld:mycheck}
	,${fld:access_type}
	,${fld:logic_type}
	,{ts '${def:timestamp}'}
	,'${def:user}'
)