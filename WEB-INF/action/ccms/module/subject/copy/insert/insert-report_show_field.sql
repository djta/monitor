INSERT	INTO
t_report_show_field
(
	tuid, report_id, field_id, show_order, is_group_by, cal_type, 
       format, width, created, createdby, updated, updatedby,  
       is_axis_x, is_axis_y, series_y, is_row_head, is_col_head, is_cross_value, 
       head_name, is_order_by, cal_type_show, document_id
)
select
	${seq:nextval@${schema}seq_default}
	,${report_id}
	,field_id, show_order, is_group_by, cal_type, 
       format, width, created, createdby, updated, updatedby,  
       is_axis_x, is_axis_y, series_y, is_row_head, is_col_head, is_cross_value, 
       head_name, is_order_by, cal_type_show, document_id
from
	t_report_show_field
where
	report_id = ${old_report_id}