select 
	d.tuid    as  id
	,d.document_name  as description
from 
	t_form f
	inner join t_document d on f.tuid = d.form_id
where
	f.pid = ${fld:form_id}
and
	f.show_type = '1' --同页显示