SELECT 
	tuid,(CASE WHEN coordinate IS NULL THEN '' ELSE coordinate END) AS coordinate 
FROM hr_org_info 
WHERE org_id = ${fld:org_id} 
union
select
null as tuid,
null as coordinate
from dual
order by tuid asc