select 
	t.tuid as field_id
	, t.field_code
	, concat((case when is_virtual_type='0' then '' else '@' end) , t.field_name_${def:locale}) as alias
	, f.show_order
	, f.width
	, f.show_type
	, f.filter_type
	, f.is_mandatory
from 
	t_report r
	inner join t_field t
	on t.table_id = r.table_id
	inner join (select a.field_id,a.show_order,a.width,a.show_type,a.filter_type,a.filter_value,a.is_mandatory from t_report_filter_field a where a.report_id = ${fld:report_id}) f 
	on t.tuid=f.field_id
where 
	r.tuid = ${fld:report_id}
order by 
	f.show_order