select
	count(1) as flag
from
	os_wfm
where
	tuid = ${fld:wfm_id}