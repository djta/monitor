INSERT	INTO
t_form_excel_field
(
	tuid, form_id, field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby
)
SELECT ${seq:nextval@${schema}seq_default}, ${form_id}, ${field_id}, show_order, width, show_type, created, 
       createdby, updated, updatedby
FROM 
	t_form_excel_field
where
	form_id = ${old_form_id}
and
	field_id = ${old_field_id}