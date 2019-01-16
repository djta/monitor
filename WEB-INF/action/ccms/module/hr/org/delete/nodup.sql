select 
	1 
from 
	hr_org 
where 
	pid= ${fld:id} 
or
	(select count(1) from hr_org_post  where org_id = ${fld:id})>0 
or
	(select count(1) from hr_staff  where org_id = ${fld:id})>0 