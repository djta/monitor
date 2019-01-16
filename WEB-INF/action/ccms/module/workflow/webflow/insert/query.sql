select
	wfm_real_name as wfm_name
from
	os_wfm
where
	tuid = ${fld:wfm_id}