select 
	node_name 
from 
	cs_job_node
where 
	job_id = ${fld:job_id}
and
	node_type = '0'		-- first node
and
	tuid != ${fld:tuid}
and
    ${fld:node_type} = '0'
