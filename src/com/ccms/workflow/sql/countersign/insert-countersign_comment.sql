INSERT	INTO
os_wfm_countersign_comment
(
	tuid
	, countersign_id
	, ${field}
	, created
	, createdby
	, snapshot
	, p_pk_value
)
VALUES
(
	  ${seq:nextval@seq_os_wfm_countersign_comment}
	, '${pk_value}'
	, '${value}'
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, 0
	, ${fld:__p_pk_value__}
)
