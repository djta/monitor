select
	tuid,
	wfm_name
from
	os_wfm
where
	is_template = '1'
and 
	wfm_status = '1'
