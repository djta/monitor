select
	distinct 
	d.*
from 
	t_table t
	inner join cc_ib_job f
	on t.tuid = f.search_table_id
	inner join t_field d
	on d.table_id = t.tuid
where 
	f.tuid = ${fld:job_id}