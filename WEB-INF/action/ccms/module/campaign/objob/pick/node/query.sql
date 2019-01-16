select 
	tuid    as  id
	, node_name  as description
	, ob_type
from 
	cs_job_node
where 
    job_id = ${fld:job_id}
and
    ob_type='0'
order by 
	id
