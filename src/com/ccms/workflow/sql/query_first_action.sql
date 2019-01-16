SELECT
	a.tuid as action_id
	,a.next_node
FROM
	os_wfm_node n
	inner join os_wfm_node_to a
	on n.tuid = a.node_id
WHERE
    n.wfm_id = ${wfm_id}
and
	n.node_type = '0'