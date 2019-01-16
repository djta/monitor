select
	f.tuid as field_tuid
	,f.field_name_${def:locale}  as  field_name
from
	cs_job j
	left join cs_job_model jm
	on j.model_id = jm.tuid
	inner join t_form m
	on j.search_form_id = m.tuid
	left join t_form_grid_field t
	on m.tuid = t.form_id
	left join t_field f
	on t.field_id = f.tuid
where
	j.tuid = ${fld:job_id}
order by
	f.show_order