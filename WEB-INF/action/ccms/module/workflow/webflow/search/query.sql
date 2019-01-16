select
	tuid,
	concat(concat('(工单流)' , wfm_name) , case when wfm_status='1' then '(启用)' else '(停用)' end) as wfm_name
from 
	os_wfm
where
	tenantry_id = ${def:tenantry}
and
	wfm_type = ${fld:wfm_type}
order by 
	wfm_name