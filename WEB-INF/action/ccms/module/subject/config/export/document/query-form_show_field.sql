select
	t.*
from 
	t_form_show_field t
	inner join t_document d on d.form_id = t.form_id
where 
	d.tuid = ${fld:document_id}
