SELECT
	t.owner
FROM
	os_currentstep t
WHERE
    t.entry_id = ${entry_id}
and
	t.step_id = ${step_id}