INSERT	INTO
t_form_excel_field
(
	tuid, form_id, field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby, version
)
SELECT ${seq:nextval@${schema}seq_default}, ${form_id}, field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby, version
FROM 
	t_form_excel_field
where
	form_id = ${fld:form_id}