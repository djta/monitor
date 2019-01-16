SELECT
	a.next_node
	,aa.authority_id
FROM
	os_wfm_node_to a
	inner join os_wfm_node_to aa
	on a.next_node = aa.node_id
where
	a.node_id = ${node_id}