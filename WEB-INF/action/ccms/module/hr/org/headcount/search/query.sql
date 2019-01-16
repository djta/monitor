select 
	post_name as post_type
	,org_post_name   
	,op.tuid as org_post_id
	,h.hc_name  
	,h.hc_id 
	,s.name   
	,s.userlogin
from 
	hr_post p 
	inner join hr_org_post op on p.post_id=op.post_id
	inner join hr_headcount h on h.org_post_id=op.tuid 
	left join hr_staff s on s.hc_id=h.hc_id
where 
	op.org_id=${fld:org_id} 
	${filter}
order by 
	h.show_order
