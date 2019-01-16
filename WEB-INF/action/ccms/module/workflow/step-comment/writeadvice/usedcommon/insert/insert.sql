INSERT	INTO
os_entry_common_used
(
	tuid
	, comments
	, created
	, createdby
	, is_deleted
)
VALUES
(
	${seq:nextval@seq_os_entry_common_used}
	,${fld:usedcommon}
	,{ts '${def:timestamp}'}
	,'${def:user}'
	,'0'
)