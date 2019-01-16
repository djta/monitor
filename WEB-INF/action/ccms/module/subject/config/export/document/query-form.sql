select
	f.*
from 
	t_form f
	inner join t_document d on d.form_id = f.tuid
where 
	d.tuid = ${fld:document_id}
