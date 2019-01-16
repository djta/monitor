UPDATE
	cs_job_node_to
SET
	node_id = ${fld:node_id}
	, next_node = ${fld:next_node}
WHERE
	tuid = ${fld:tuid}
