select 
	distinct 
	t.tuid	as 	id 
	,t.category_name	as description
from 
	t_faq_category t
where
	t.staff_id = '${def:user}'	
and
	upper (category_name) like upper (${fld:name})
		${orderby}
