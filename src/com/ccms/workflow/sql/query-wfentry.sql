select
	e.id as entry_id
	,e.wfm_id
from	
	os_wfentry e
	inner join t_table et
	on e.table_id = et.tuid
	inner join t_table ef
	on lower(et.table_code) = lower(ef.table_code)
	inner join t_form f
	on f.table_id = ef.tuid
where
	e.pk_value = ${fld:pk_value}
and
	f.tuid = ${fld:form_id}