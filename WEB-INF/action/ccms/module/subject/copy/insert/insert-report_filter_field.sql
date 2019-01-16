INSERT	INTO
t_report_filter_field
(
	tuid, report_id, field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby,  filter_type, filter_value, 
       is_mandatory
)
select
	${seq:nextval@${schema}seq_default}
	,${report_id}
	,field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby,  filter_type, filter_value, 
       is_mandatory
from
	t_report_filter_field
where
	report_id = ${old_report_id}