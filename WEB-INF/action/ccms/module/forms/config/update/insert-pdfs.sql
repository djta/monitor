INSERT INTO
	t_form_pdf_field
(
	tuid
	,form_id
	,field_id
	,show_order
	,format
	,show_type
	,width
)
VALUES
(
	${seq:nextval@${schema}seq_default}
	,${fld:form_id}
	,${fld:pdf_field}
	,${fld:pdf_show_order}
	,${fld:pdf_format}
	,${fld:pdf_show_type}
	,${fld:pdf_width}
)
