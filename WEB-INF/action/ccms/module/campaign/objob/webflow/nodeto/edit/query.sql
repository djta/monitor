SELECT
    t.tuid
    ,t.node_id
    ,t.next_node
    ,t.remark
FROM
	cs_job_node_to t
WHERE
	t.tuid=${fld:id}
