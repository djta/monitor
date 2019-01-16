select 
	tuid as wfm_id
	,wfm_status
from	     
	os_wfm
where 
	xml_release is not null
and
	wfm_real_name = '${name}'