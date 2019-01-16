select 
  a.tuid
  ,a.authority_name
  ,a.remark
  ,case when ar.tuid is null then ' ' else 'checked' end as checkr_flag
  ,ar.logic_type as logic_type
from 
   hr_authority a
   left join hr_authority_relation ar on a.tuid = ar.authority_id and ar.group_id=${fld:group_id} and ar.access_type=${fld:check_status}
where 
	a.tenantry_id =${def:tenantry}
and
not exists
  ( select 1 from hr_authority_relation where access_type=${fld:status} and a.tuid=hr_authority_relation.authority_id and group_id=${fld:group_id})
