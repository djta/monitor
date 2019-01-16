select 
	node_name 
from 
	cs_job_node
where 
	job_id = (select job_id from cs_job_node where tuid=${fld:id})
and
	node_type = '0'		-- first node
and
	tuid != ${fld:id}