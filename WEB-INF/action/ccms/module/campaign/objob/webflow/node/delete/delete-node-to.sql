delete from 
	cs_job_node_to
where 
	node_id = ${fld:id}
or
	next_node = ${fld:id}