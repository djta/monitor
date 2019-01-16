INSERT	INTO
    os_wfm_help
(
	tuid
	, wfm_id
	, node_id
	, created
	, createdby
	,node_help
)
VALUES
(
	  ${fld:tuid}
	, ${fld:wfm_id}
	, ${fld:node_id}
	, {ts '${def:timestamp}'}
	, '${def:user}'
	,?
)