select
	f.show_name,
	count(t.tuid) as total
from
	t_faq f
	left join t_faq_click t
	on t.faq_id = f.tuid

where
	f.tenantry_id = ${def:tenantry}

${filter}

group by 
	f.tuid,f.show_name

order by 
	count(t.tuid) desc
