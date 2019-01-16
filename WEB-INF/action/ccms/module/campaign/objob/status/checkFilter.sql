select 
	max(tf.job_id) as job_id
from
	cs_job_filter tf
	inner join cs_job tj
	on tf.job_id = tj.tuid
where
	tf.job_id = ${fld:id}
or
	tj.parent_id is not null
