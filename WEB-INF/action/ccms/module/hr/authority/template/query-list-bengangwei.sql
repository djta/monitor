select
	g.org_id
	,g.org_name
	,p.tuid as post_id
	,p.org_post_name as post_name
	,f.userlogin
	,f.name as staff_name
from
	hr_staff f
	inner join ${schema}s_user u on f.userlogin = u.userlogin and u.enabled = 1
	inner join hr_post_staff ps on f.userlogin = ps.userlogin
	inner join hr_org_post p on ps.org_post_id = p.tuid
	inner join hr_org g on f.org_id = g.org_id
where
	exists(
		select 1 from hr_staff hf inner join hr_post_staff hps on hr.userlogin = hps.userlogin
		where hf.userlogin = '${staff}' and p.tuid = hps.org_post_id
	)