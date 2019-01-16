SELECT
	t.countersign_type
	,t.countersign_per
FROM
	os_wfm_node t
WHERE
    t.tuid = ${id}
and
	t.step_type = '3'
