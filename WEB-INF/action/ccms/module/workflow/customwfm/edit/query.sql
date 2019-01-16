select
	tuid,
	wfm_name,
	wfm_status,
	remark,
	table_id,
	(select table_name from t_table WHERE tuid = table_id) AS table_name,
	logo_path
from 
	os_wfm
where
	tuid = ${fld:id}