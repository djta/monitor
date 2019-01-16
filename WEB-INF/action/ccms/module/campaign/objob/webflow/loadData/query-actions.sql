SELECT
	t.tuid
	,t.node_id
	,t.next_node
FROM
	cs_job_node_to t
	left join cs_job_node n
	on t.node_id = n.tuid
WHERE
	n.job_id = ${fld:job_id}