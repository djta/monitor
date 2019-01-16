INSERT	INTO
	os_currentstep
(
	id
	, entry_id
	, step_id
	, start_date
	, status
)
SELECT
	${seq:nextval@seq_os_currentsteps}
	,h.entry_id
	,h.step_id
	,{ts '${def:timestamp}'}
	,n.status
FROM
	os_historystep h
	inner join os_wfm_node n on h.step_id = n.tuid
WHERE
	h.id = ${fld:history_id}
