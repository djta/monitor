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
union
select 
	'' as name_name
from dual
where
	${fld:node_type} = '0'