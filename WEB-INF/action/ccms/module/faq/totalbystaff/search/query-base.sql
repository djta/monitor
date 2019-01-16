select
	f.show_name,
	s.name,
	count(t.tuid) as total
from
	t_faq f
	left join t_faq_click t
	on t.faq_id = f.tuid
	left join hr_staff s
	on t.user_id = s.userlogin
where
	f.tenantry_id = ${def:tenantry}

${filter}

group by 
	f.tuid,t.user_id,f.show_name,s.name

order by count(t.tuid) desc
