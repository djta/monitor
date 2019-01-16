select
	t.tuid,
	t.superior_id,
	t.show_name as show_name,
	h.tuid as headline_id
from 
	t_faq t 
	left join t_faq_headline h
	on t.tuid = h.faq_id
where
	t.faq_type = '0' 
and
	t.tenantry_id = ${def:tenantry}
and 
	(t.is_delete = '0' or t.is_delete is null or t.is_delete = '')
order by t.superior_id asc
