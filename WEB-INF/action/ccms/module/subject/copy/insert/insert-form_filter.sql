INSERT	INTO
t_form_filter_field
(
	tuid, form_id, field_id, show_order, created, createdby, updated, 
       updatedby,  width, show_type, filter_type, item_id, colspan, rowspan, is_mandatory
)
SELECT ${seq:nextval@${schema}seq_default}, ${form_id}, ${field_id}, show_order, created, createdby, updated, 
       updatedby,  width, show_type, filter_type, ${item_id}, colspan, rowspan, is_mandatory
FROM 
	t_form_filter_field
where
	form_id = ${old_form_id}
and
	field_id = ${old_field_id}
and
	item_id = ${old_item_id}