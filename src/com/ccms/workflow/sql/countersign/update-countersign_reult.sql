update
	os_wfm_countersign
set
	result = '${result}'
where
	tuid = (select pk_value from os_wfentry where id=${id})