INSERT	INTO
t_form_pdf_field
(
	tuid, form_id, field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby,  format
)
SELECT ${seq:nextval@${schema}seq_default}, ${form_id}, field_id, show_order, width, show_type, created, 
       createdby, updated, updatedby,  format
FROM 
	t_form_pdf_field
where
	form_id = ${fld:form_id}