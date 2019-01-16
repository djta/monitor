SELECT
	t.step_type
FROM
	os_wfm_node t
WHERE
    t.tuid = ${id}
