select
	t.tuid as field_id
	, t.field_code
	, concat((case when is_virtual_type='0' then '' else '@' end) , t.field_name_${def:locale}) as alias
	, f.show_order
	, f.is_group_by
	, f.cal_type
	, f.is_row_head
	, f.is_col_head
	, f.is_cross_value
	, f.head_name
	, f.is_order_by
	, f.cal_type_show
	, f.document_id
	, d.document_name
	, f.format
from 
	t_report r
	inner join t_field t	on t.table_id = r.table_id
	inner join (select a.field_id,a.show_order,a.is_group_by,a.cal_type,a.format,a.width,a.is_axis_x,a.is_axis_y,a.series_y,a.is_row_head,a.is_col_head,a.is_cross_value,a.head_name,a.is_order_by,a.cal_type_show,a.document_id from t_report_show_field a where a.report_id = ${fld:report_id}) f on t.tuid=f.field_id
	left join t_document d on f.document_id=d.tuid
where 
	r.tuid = ${fld:report_id}
order by 
	f.show_order
