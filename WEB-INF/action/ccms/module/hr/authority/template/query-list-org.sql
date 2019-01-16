select
	g.org_id
	,g.org_name
	,p.tuid as post_id
	,p.org_post_name as post_name
	,f.userlogin
	,f.name as staff_name
from
	hr_authority_list t
	inner join hr_org g on t.entity_value = cast(g.org_id as varchar)
	inner join hr_staff f on g.org_id = f.org_id
	inner join ${schema}s_user u on f.userlogin = u.userlogin and u.enabled = 1
	left join hr_post_staff ps on f.userlogin = ps.userlogin
	left join hr_org_post p on ps.org_post_id = p.tuid
where
	t.entity_id = ${entity_id}