INSERT	INTO
os_entry_comment
(
	tuid
	, entry_id
	, node_id
	, comments
	, created
	, createdby
	, snapshot
)
VALUES
(
	${seq:nextval@seq_os_entry_comment}
	,${fld:entry_id}
	,${fld:node_id}
	,${fld:owenadvice}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,0
)