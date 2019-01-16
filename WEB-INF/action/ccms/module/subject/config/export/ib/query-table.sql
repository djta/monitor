select
	distinct 
	t.*
from 
	t_table t
	inner join cc_ib_job f
	on t.tuid = f.search_table_id
where 
	f.tuid = ${fld:job_id}