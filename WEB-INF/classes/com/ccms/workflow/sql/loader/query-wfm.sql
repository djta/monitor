select 
	tuid as wfm_id
	,wfm_real_name as wfm_name
from	     
	os_wfm
where 
	wfm_status = '1'
and
	xml_release is not null