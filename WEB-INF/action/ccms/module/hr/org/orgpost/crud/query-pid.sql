select 
t2.tuid,
t2.org_post_name
from hr_org t1
join hr_org_post t2 on t1.org_id=t2.org_id
where 
charIndex(org_path,(select org_path from hr_org where org_id=${fld:org_id}))>0
and
t1.org_id!=${fld:org_id}