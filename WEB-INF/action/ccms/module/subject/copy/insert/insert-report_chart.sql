INSERT	INTO
t_report_chart
(
	tuid, report_id, remark, created, createdby, updated, updatedby, 
       version, title, title_x, title_y, title_z, field_x, field_y, 
       field_z, format_x, format_y, format_z, chart_type, is_3d, callback_js
)
select
	${seq:nextval@${schema}seq_default}
	,${report_id}
	, remark, created, createdby, updated, 
       updatedby,version, title, title_x, title_y, title_z, field_x, field_y, 
       field_z, format_x, format_y, format_z, chart_type, is_3d, callback_js
from
	t_report_chart
where
	report_id = ${old_report_id}