select 
	t.tuid    as  field_id
	, t.field_code
	, concat((case when is_virtual_type='0' then '' else '@' end) , t.field_name) as alias
from 
	t_report fm
	inner join t_field t
	on t.table_id = fm.table_id
where 
	fm.tuid = ${fld:report_id}
	and t.deleted = '0'
order by 
	t.show_order
