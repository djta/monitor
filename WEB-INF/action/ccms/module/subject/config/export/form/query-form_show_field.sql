select
	t.*
from 
	t_form_show_field t
where 
	t.form_id = ${fld:form_id}