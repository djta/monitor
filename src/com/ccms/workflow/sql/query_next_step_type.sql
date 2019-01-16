SELECT
	t.step_type
FROM
	os_wfm_node_to a
	inner join os_wfm_node t
	on a.next_node = t.tuid
WHERE
    a.tuid = ${action_id}