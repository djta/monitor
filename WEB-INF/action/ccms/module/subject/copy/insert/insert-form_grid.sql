INSERT	INTO
t_form_grid_field
(
	tuid, form_id, field_id, show_order, created, createdby, updated, 
       updatedby,  width, show_flag, format, sort_order, print_html, compute_total, show_align
)
SELECT ${seq:nextval@${schema}seq_default}, ${form_id}, ${field_id}, show_order, created, createdby, updated, 
       updatedby,  width, show_flag, format, sort_order, print_html, compute_total, show_align
FROM 
	t_form_grid_field
where
	form_id = ${old_form_id}
and
	field_id = ${old_field_id}