select 1 
from hr_org_holiday 
where {ts '${def:timestamp}'} >= begintime 
and {ts '${def:timestamp}'} <= endtime 
and org_id = ${fld:org_id} and status = 1
