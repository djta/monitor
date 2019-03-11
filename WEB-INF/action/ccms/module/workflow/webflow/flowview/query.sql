select
org_name
,(select 1 from os_wfm  where tuid=${fld:org_id}) as is_load
from
hr_org
where 
org_id=${fld:org_id}