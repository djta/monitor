INSERT	INTO
os_wfm_countersign_list
(
	tuid
	, countersign_id
	, pk_value
	, created
	, createdby
	, snapshot
)
VALUES
(
	  ${seq:nextval@seq_os_wfm_countersign_list}
	, '${pk_value}'
	, '${value}'
	, {ts '${def:timestamp}'}
	, '${def:user}'
	, 0
)
