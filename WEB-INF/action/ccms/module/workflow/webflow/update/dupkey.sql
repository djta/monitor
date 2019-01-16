select 
	wfm_name
from 
	os_wfm
where 
	wfm_name = ${fld:wfm_name}
and
	tuid <> ${fld:wfm_id}
and
	tenantry_id = ${def:tenantry}