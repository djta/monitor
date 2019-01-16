select
	g.org_id
	,g.org_name
	,p.tuid as post_id
	,p.org_post_name as post_name
	,f.userlogin
	,f.name as staff_name
from
	hr_authority_list t
	inner join hr_org_post p on p.tuid = cast(t.entity_value as integer)
	inner join hr_post_staff ps on ps.org_post_id = p.tuid
	inner join hr_staff f on f.userlogin = ps.userlogin
	inner join ${schema}s_user u on f.userlogin = u.userlogin and u.enabled = 1
	left join hr_org g on f.org_id = g.org_id
where
	t.entity_id = ${entity_id}