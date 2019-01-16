select
pid
from
hr_org
where
'%'||org_id||'%'=${fld:org_id}
