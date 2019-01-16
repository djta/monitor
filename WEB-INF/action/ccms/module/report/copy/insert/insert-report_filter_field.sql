INSERT	INTO
t_report_filter_field
(
	tuid, report_id, field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby, version, filter_type, filter_value, 
       is_mandatory
)
select
	${seq:nextval@${schema}seq_default}
	,${report_id}
	,field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby, version, filter_type, filter_value, 
       is_mandatory
from
	t_report_filter_field
where
	report_id = ${fld:report_id}