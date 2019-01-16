delete from 
	cs_job_node_rule
where
	exists(
		select tuid from cs_job_node_to where (node_id = ${fld:id} or next_node = ${fld:id}) and tuid = cs_job_node_rule.node_to_id
	)