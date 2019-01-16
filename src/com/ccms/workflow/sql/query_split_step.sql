SELECT
	distinct 
	t.node_id
FROM
	os_wfm_node_to t
	inner join os_wfm_node n
	on t.node_id = n.tuid
WHERE
    t.next_node = ${id}
and
	n.step_type = '0'
and
	t.node_id != t.next_node