select
	1 
from 
	hr_org_post 
where 
	pid= ${fld:id} 
or
	(select count(1) from hr_post_staff where org_post_id = ${fld:id})>0 
or
	(select count(1) from hr_headcount where org_post_id = ${fld:id})>0 